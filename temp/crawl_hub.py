from __future__ import annotations

import html
import re
import sys
import time
from collections import defaultdict, deque
from dataclasses import dataclass, field
from html.parser import HTMLParser
from http.cookiejar import CookieJar
from urllib.error import HTTPError, URLError
from urllib.parse import parse_qsl, quote, urlencode, urljoin, urlparse, urlunparse
from urllib.request import HTTPCookieProcessor, Request, build_opener


BASE = "http://localhost:8080"
START = BASE + "/hub/"
EMAIL = "nbunker@immregistries.org"
MAX_PAGES = 350
MAX_PER_SIGNATURE = 3

STATIC_EXTENSIONS = {
    ".css",
    ".js",
    ".png",
    ".jpg",
    ".jpeg",
    ".gif",
    ".svg",
    ".ico",
    ".webp",
    ".pdf",
    ".zip",
    ".gz",
    ".txt",
    ".xml",
    ".json",
    ".woff",
    ".woff2",
}


def normalize_url(url: str, current: str = START) -> str | None:
    if not url:
        return None
    url = html.unescape(url.strip())
    if not url or url.startswith(("#", "mailto:", "tel:", "javascript:")):
        return None
    absolute = urljoin(current, url)
    parsed = urlparse(absolute)
    if parsed.scheme not in ("http", "https"):
        return None
    path = re.sub(r"/+", "/", parsed.path)
    query = urlencode(sorted(parse_qsl(parsed.query, keep_blank_values=True)), doseq=True)
    return urlunparse((parsed.scheme, parsed.netloc, path, "", query, ""))


def is_static(url: str) -> bool:
    path = urlparse(url).path.lower()
    return any(path.endswith(ext) for ext in STATIC_EXTENSIONS)


class LinkParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self.title = ""
        self.links: list[tuple[str, str]] = []
        self.forms: list[dict[str, str]] = []
        self.headings: list[tuple[str, str]] = []
        self.current_link: dict[str, str] | None = None
        self.current_heading: str | None = None
        self.in_title = False
        self._title_parts: list[str] = []
        self._link_parts: list[str] = []
        self._heading_parts: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        attr = {k.lower(): v or "" for k, v in attrs}
        if tag == "title":
            self.in_title = True
            self._title_parts = []
        elif tag == "a" and attr.get("href"):
            self.current_link = {"href": attr["href"]}
            self._link_parts = []
        elif tag == "form":
            self.forms.append(
                {
                    "method": (attr.get("method") or "get").upper(),
                    "action": attr.get("action") or "",
                }
            )
        elif tag in {"h1", "h2", "h3"}:
            self.current_heading = tag
            self._heading_parts = []

    def handle_endtag(self, tag: str) -> None:
        if tag == "title":
            self.in_title = False
            self.title = clean_text(" ".join(self._title_parts))
        elif tag == "a" and self.current_link is not None:
            text = clean_text(" ".join(self._link_parts))
            self.links.append((self.current_link["href"], text))
            self.current_link = None
        elif tag == self.current_heading:
            text = clean_text(" ".join(self._heading_parts))
            if text:
                self.headings.append((tag, text))
            self.current_heading = None

    def handle_data(self, data: str) -> None:
        if self.in_title:
            self._title_parts.append(data)
        if self.current_link is not None:
            self._link_parts.append(data)
        if self.current_heading is not None:
            self._heading_parts.append(data)


def clean_text(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip()


@dataclass
class Page:
    url: str
    status: int
    title: str = ""
    headings: list[tuple[str, str]] = field(default_factory=list)
    links: list[tuple[str, str]] = field(default_factory=list)
    forms: list[dict[str, str]] = field(default_factory=list)
    content_type: str = ""
    error: str = ""
    inbound: set[str] = field(default_factory=set)


class HubCrawler:
    def __init__(self) -> None:
        self.jar = CookieJar()
        self.opener = build_opener(HTTPCookieProcessor(self.jar))
        self.pages: dict[str, Page] = {}
        self.edges: dict[str, set[str]] = defaultdict(set)
        self.link_text: dict[tuple[str, str], set[str]] = defaultdict(set)
        self.external: dict[str, set[str]] = defaultdict(set)
        self.off_hub: dict[str, set[str]] = defaultdict(set)
        self.discovered_hub: dict[str, set[str]] = defaultdict(set)

    def request(self, url: str, data: bytes | None = None) -> tuple[int, str, str, str]:
        req = Request(url, data=data, headers={"User-Agent": "InteropHub sitemap crawler"})
        if data is not None:
            req.add_header("Content-Type", "application/x-www-form-urlencoded")
        try:
            with self.opener.open(req, timeout=15) as resp:
                raw = resp.read()
                charset = resp.headers.get_content_charset() or "utf-8"
                return resp.status, resp.geturl(), resp.headers.get("Content-Type", ""), raw.decode(charset, "replace")
        except HTTPError as exc:
            raw = exc.read()
            charset = exc.headers.get_content_charset() or "utf-8"
            return exc.code, exc.geturl(), exc.headers.get("Content-Type", ""), raw.decode(charset, "replace")
        except URLError as exc:
            return 0, url, "", str(exc)

    def login(self) -> str:
        body = urlencode({"email": EMAIL}).encode("utf-8")
        status, _, _, content = self.request(BASE + "/hub/send-welcome-email", body)
        if status != 200:
            raise RuntimeError(f"Login email POST failed: HTTP {status}")
        match = re.search(r'href="([^"]*/hub/magic-link\?token=[^"]+)"', content)
        if not match:
            raise RuntimeError("Could not find dev magic-link in login response")
        magic = html.unescape(match.group(1))
        status, final_url, _, _ = self.request(magic)
        if status not in (200, 302):
            raise RuntimeError(f"Magic-link login failed: HTTP {status}")
        token = parse_qsl(urlparse(magic).query)
        token_value = next((value for key, value in token if key == "token"), "")
        if not token_value:
            raise RuntimeError("Magic-link did not include a token")
        confirm_body = urlencode({"token": token_value}).encode("utf-8")
        status, final_url, _, _ = self.request(BASE + "/hub/magic-link", confirm_body)
        if status not in (200, 302):
            raise RuntimeError(f"Magic-link confirmation failed: HTTP {status}")
        return final_url

    def crawl(self) -> None:
        login_final = self.login()
        start_candidates = [normalize_url(login_final), BASE + "/hub/home", BASE + "/hub/"]
        queue = deque(url for url in start_candidates if url)
        seen: set[str] = set()
        signature_fetch_count: dict[str, int] = defaultdict(int)
        while queue and len(self.pages) < MAX_PAGES:
            url = queue.popleft()
            if url in seen or is_static(url):
                continue
            seen.add(url)
            parsed = urlparse(url)
            if parsed.netloc not in ("localhost:8080", "127.0.0.1:8080") or not parsed.path.startswith("/hub"):
                continue
            sig = route_signature(url)
            if signature_fetch_count[sig] >= MAX_PER_SIGNATURE:
                continue
            signature_fetch_count[sig] += 1
            status, final_url, content_type, content = self.request(url)
            final = normalize_url(final_url, url) or url
            page = Page(url=final, status=status, content_type=content_type)
            self.pages[final] = page
            if "text/html" not in content_type.lower():
                continue
            parser = LinkParser()
            parser.feed(content)
            page.title = parser.title
            page.headings = parser.headings
            page.forms = [
                {
                    "method": form["method"],
                    "action": normalize_url(form["action"], final) or form["action"],
                }
                for form in parser.forms
            ]
            page.links = []
            for raw_href, text in parser.links:
                target = normalize_url(raw_href, final)
                if not target:
                    continue
                page.links.append((target, text))
                target_parsed = urlparse(target)
                if target_parsed.netloc in ("localhost:8080", "127.0.0.1:8080") and target_parsed.path.startswith("/hub"):
                    self.discovered_hub[route_signature(target)].add(target)
                    if not is_static(target):
                        self.edges[final].add(target)
                        self.link_text[(final, target)].add(text or "(no text)")
                        if target not in seen:
                            queue.append(target)
                    continue
                if target_parsed.netloc in ("localhost:8080", "127.0.0.1:8080"):
                    self.off_hub[target].add(final)
                else:
                    self.external[target].add(final)
            time.sleep(0.03)

    def report(self) -> str:
        lines: list[str] = []
        lines.append("# InteropHub Local Site Map Catalog")
        lines.append("")
        lines.append(f"Generated from `{START}` after logging in as `{EMAIL}`.")
        lines.append("Only `/hub` pages were recursively crawled. Links to other localhost apps and external sites were recorded but not followed.")
        lines.append("")
        lines.append("## Crawl Summary")
        lines.append("")
        lines.append(f"- Hub pages fetched: {len(self.pages)}")
        lines.append(f"- Route/page-type signatures represented: {len({route_signature(url) for url in self.pages})}")
        lines.append(f"- In-scope navigation edges: {sum(len(v) for v in self.edges.values())}")
        lines.append(f"- Other localhost links documented but not crawled: {len(self.off_hub)}")
        lines.append(f"- External links documented but not crawled: {len(self.external)}")
        lines.append("")
        lines.append("## Primary Navigation Groups")
        lines.append("")
        grouped: dict[str, dict[str, list[Page]]] = defaultdict(lambda: defaultdict(list))
        for page in self.pages.values():
            parts = [p for p in urlparse(page.url).path.split("/") if p]
            key = "/" + "/".join(parts[:2]) if len(parts) >= 2 else urlparse(page.url).path
            grouped[key][route_signature(page.url)].append(page)
        for key in sorted(grouped):
            lines.append(f"### `{key}`")
            for sig in sorted(grouped[key]):
                pages = sorted(grouped[key][sig], key=lambda p: p.url)
                page = pages[0]
                label = page.title or first_heading(page) or urlparse(page.url).path
                discovered_count = len(self.discovered_hub.get(sig, set()) | {p.url for p in pages})
                suffix = f"; {discovered_count} variants discovered" if discovered_count > 1 else ""
                examples = ", ".join(f"`{display_url(p.url)}`" for p in pages[:3])
                lines.append(f"- `{sig}` - {label}{suffix}. Examples: {examples}")
            lines.append("")
        lines.append("## Page Catalog")
        lines.append("")
        for url in sorted(self.pages):
            page = self.pages[url]
            lines.append(f"### `{display_url(url)}`")
            if page.status != 200:
                lines.append(f"- Status: HTTP {page.status}")
            title = page.title or "(untitled)"
            lines.append(f"- Title: {title}")
            headings = [text for _, text in page.headings[:6]]
            if headings:
                lines.append(f"- Visible headings: {', '.join(headings)}")
            lines.append(f"- Summary: {summarize(page)}")
            inbound = sorted(display_url(src) for src in page.inbound)
            if inbound:
                lines.append(f"- Reached from: {', '.join(inbound[:8])}")
            children = sorted(self.edges.get(url, set()))
            if children:
                rendered = []
                for child in children[:20]:
                    labels = sorted(self.link_text.get((url, child), []))
                    rendered.append(f"`{display_url(child)}` ({'; '.join(labels[:3])})")
                lines.append(f"- Links deeper within `/hub`: {', '.join(rendered)}")
            forms = [form for form in page.forms if form["action"]]
            if forms:
                form_bits = [f"{form['method']} `{display_url(form['action'])}`" for form in forms[:8]]
                lines.append(f"- Forms/actions present: {', '.join(form_bits)}")
            lines.append("")
        if self.off_hub:
            lines.append("## Other Localhost Applications Linked But Not Crawled")
            lines.append("")
            for target, sources in sorted(self.off_hub.items()):
                lines.append(f"- `{display_url(target)}` from {', '.join(sorted(display_url(s) for s in sources)[:6])}")
            lines.append("")
        if self.external:
            lines.append("## External Links Linked But Not Crawled")
            lines.append("")
            for target, sources in sorted(self.external.items()):
                lines.append(f"- `{target}` from {', '.join(sorted(display_url(s) for s in sources)[:6])}")
            lines.append("")
        return "\n".join(lines)


def first_heading(page: Page) -> str:
    return page.headings[0][1] if page.headings else ""


def display_url(url: str) -> str:
    parsed = urlparse(url)
    if parsed.netloc in ("localhost:8080", "127.0.0.1:8080"):
        return urlunparse(("", "", parsed.path, "", parsed.query, ""))
    return url


def route_signature(url: str) -> str:
    parsed = urlparse(url)
    parts = [normalize_path_part(part) for part in parsed.path.split("/") if part]
    path = "/" + "/".join(parts)
    query = parse_qsl(parsed.query, keep_blank_values=True)
    if not query:
        return path
    rendered: list[tuple[str, str]] = []
    for key, value in sorted(query):
        if key in {"action", "mode", "view", "month", "year"}:
            rendered.append((key, value))
        else:
            rendered.append((key, sample_query_value(key, value)))
    query = "&".join(f"{quote(key)}={quote(value, safe='{}')}" for key, value in rendered)
    return path + "?" + query


def normalize_path_part(part: str) -> str:
    if part.isdigit():
        return "{id}"
    if re.fullmatch(r"[A-Z]{2,}\d{2,4}", part):
        return "{code}"
    return part


def sample_query_value(key: str, value: str) -> str:
    if key.lower().endswith("id") or key in {"id", "space"}:
        return "{id}"
    if key.lower().endswith("code"):
        return "{code}"
    if len(value) > 28 or "/" in value or "?" in value:
        return "{value}"
    return "{value}"


def summarize(page: Page) -> str:
    path = urlparse(page.url).path
    bits = [page.title, first_heading(page)]
    text = " ".join(bit for bit in bits if bit).lower()
    if page.status != 200:
        return "Non-success response encountered during crawl."
    if "admin" in path or "admin" in text:
        return "Administrative screen for managing InteropHub configuration, participants, organizations, reports, or operational data."
    if path in ("/hub/", "/hub/home") or "interophub login" in text:
        return "Entry or landing area for signing in and reaching the main InteropHub workspace."
    if "connectathon" in path or "connectathon" in text:
        return "Connectathon workspace page for viewing or managing event-specific collaboration information."
    if "participant" in path or "profile" in path or "account" in path:
        return "Participant/account page for viewing or editing user-facing identity and profile information."
    if "api" in path or "secret" in path or "credential" in text:
        return "Developer/API access page for credentials, secrets, or integration details."
    if "organization" in path or "org" in path:
        return "Organization page for viewing or managing organization records."
    if "system" in path or "demo" in path:
        return "System or demonstration-resource page linked from the InteropHub workspace."
    if page.forms:
        return "Interactive page with one or more forms; likely used to view, filter, create, or update InteropHub data."
    if page.links:
        return "Navigation page that links to related InteropHub pages or resources."
    return "Standalone content page discovered during the crawl."


def main() -> int:
    crawler = HubCrawler()
    crawler.crawl()
    for source, targets in crawler.edges.items():
        for target in targets:
            if target in crawler.pages:
                crawler.pages[target].inbound.add(source)
    report = crawler.report()
    output = "hub-site-map-catalog.md"
    with open(output, "w", encoding="utf-8", newline="\n") as f:
        f.write(report)
    print(f"Wrote {output}")
    print(f"Hub pages fetched: {len(crawler.pages)}")
    print(f"Other localhost links: {len(crawler.off_hub)}")
    print(f"External links: {len(crawler.external)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
