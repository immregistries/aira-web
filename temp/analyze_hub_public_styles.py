from __future__ import annotations

import html
import re
import sys
from collections import Counter, defaultdict
from html.parser import HTMLParser
from http.cookiejar import CookieJar
from urllib.parse import urlencode, urljoin
from urllib.request import HTTPCookieProcessor, Request, build_opener


BASE = "http://localhost:8080"
EMAIL = "nbunker@immregistries.org"

PUBLIC_PATHS = [
    "/hub/",
    "/hub/home",
    "/hub/welcome",
    "/hub/register/DD26",
    "/hub/register/complete/DD26",
    "/hub/registerForMeeting/DD26/Immunization-CDS",
    "/hub/topics/DD26",
    "/hub/table/DD26/1?view=vote",
    "/hub/table/DD26/1?view=results",
    "/hub/attend/Immunization-CDS",
    "/hub/attend/Immunization-CDS?anon=1",
    "/hub/es/topics?space=emerging-standards",
    "/hub/es/topics?space=emerging-standards&view=all",
    "/hub/es/topics?space=emerging-standards&view=meetings",
    "/hub/es/topic/1",
    "/hub/es/review/DD26",
    "/hub/es/cdc-review/DD26",
    "/hub/es/agenda?meetingId=12",
    "/hub/es/meetings?seriesId=3",
    "/hub/es/meeting-poll?pollId=1",
]


class ClassParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self.title = ""
        self.body_class = ""
        self.classes: Counter[str] = Counter()
        self.tags: Counter[str] = Counter()
        self.forms: list[str] = []
        self.headings: list[str] = []
        self.inline_styles: list[str] = []
        self._in_title = False
        self._in_style = False
        self._title_parts: list[str] = []
        self._style_parts: list[str] = []
        self._heading_tag: str | None = None
        self._heading_parts: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        attr = {k.lower(): v or "" for k, v in attrs}
        self.tags[tag] += 1
        if tag == "title":
            self._in_title = True
            self._title_parts = []
        if tag == "style":
            self._in_style = True
            self._style_parts = []
        if tag == "body":
            self.body_class = attr.get("class", "")
        if attr.get("class"):
            for cls in attr["class"].split():
                self.classes[cls] += 1
        if tag == "form":
            method = (attr.get("method") or "get").upper()
            self.forms.append(f"{method} {attr.get('action') or ''}".strip())
        if tag in {"h1", "h2", "h3"}:
            self._heading_tag = tag
            self._heading_parts = []

    def handle_endtag(self, tag: str) -> None:
        if tag == "title":
            self._in_title = False
            self.title = clean(" ".join(self._title_parts))
        if tag == "style":
            self._in_style = False
            self.inline_styles.append("\n".join(self._style_parts))
        if tag == self._heading_tag:
            value = clean(" ".join(self._heading_parts))
            if value:
                self.headings.append(value)
            self._heading_tag = None

    def handle_data(self, data: str) -> None:
        if self._in_title:
            self._title_parts.append(data)
        if self._in_style:
            self._style_parts.append(data)
        if self._heading_tag:
            self._heading_parts.append(data)


def clean(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip()


class Client:
    def __init__(self) -> None:
        self.opener = build_opener(HTTPCookieProcessor(CookieJar()))

    def fetch(self, path: str, data: bytes | None = None) -> tuple[int, str]:
        url = path if path.startswith("http") else BASE + path
        req = Request(url, data=data, headers={"User-Agent": "InteropHub public style analyzer"})
        if data is not None:
            req.add_header("Content-Type", "application/x-www-form-urlencoded")
        with self.opener.open(req, timeout=15) as resp:
            raw = resp.read()
            charset = resp.headers.get_content_charset() or "utf-8"
            return resp.status, raw.decode(charset, "replace")

    def login(self) -> None:
        _, content = self.fetch("/hub/send-welcome-email", urlencode({"email": EMAIL}).encode("utf-8"))
        match = re.search(r'href="([^"]*/hub/magic-link\?token=([^"&]+)[^"]*)"', content)
        if not match:
            raise RuntimeError("Could not find magic link")
        token = html.unescape(match.group(2))
        self.fetch(match.group(1))
        self.fetch("/hub/magic-link", urlencode({"token": token}).encode("utf-8"))


def css_selectors(css: str) -> list[str]:
    without_comments = re.sub(r"/\*.*?\*/", "", css, flags=re.S)
    selectors: list[str] = []
    for match in re.finditer(r"([^{}]+)\{", without_comments):
        block = match.group(1).strip()
        if block.startswith("@"):
            continue
        for selector in block.split(","):
            selector = clean(selector)
            if selector:
                selectors.append(selector)
    return selectors


def css_classes(selectors: list[str]) -> Counter[str]:
    classes: Counter[str] = Counter()
    for selector in selectors:
        for cls in re.findall(r"\.([A-Za-z][A-Za-z0-9_-]*)", selector):
            classes[cls] += 1
    return classes


def main() -> int:
    client = Client()
    client.login()
    _, css = client.fetch("/hub/css/main.css")
    selectors = css_selectors(css)
    css_class_counts = css_classes(selectors)
    inline_selectors_by_page: dict[str, list[str]] = {}
    inline_class_counts: Counter[str] = Counter()
    page_results = []
    html_class_counts: Counter[str] = Counter()
    body_classes: Counter[str] = Counter()
    for path in PUBLIC_PATHS:
        status, content = client.fetch(path)
        parser = ClassParser()
        parser.feed(content)
        inline_selectors: list[str] = []
        for style in parser.inline_styles:
            inline_selectors.extend(css_selectors(style))
        inline_selectors_by_page[path] = inline_selectors
        inline_class_counts.update(css_classes(inline_selectors))
        html_class_counts.update(parser.classes)
        for cls in parser.body_class.split():
            body_classes[cls] += 1
        page_results.append((path, status, parser))

    prefixes: dict[str, int] = defaultdict(int)
    for cls in css_class_counts + inline_class_counts:
        prefix = cls.split("-", 1)[0] if "-" in cls else cls
        prefixes[prefix] += 1

    lines: list[str] = []
    lines.append("# InteropHub Public Style Snapshot")
    lines.append("")
    lines.append(f"Sampled {len(page_results)} public-oriented pages after logging in as `{EMAIL}`.")
    lines.append(f"Parsed {len(selectors)} CSS selectors and {len(css_class_counts)} unique CSS class selectors from `/hub/css/main.css`.")
    lines.append(f"Parsed {sum(len(v) for v in inline_selectors_by_page.values())} selectors and {len(inline_class_counts)} unique CSS class selectors from sampled inline `<style>` blocks.")
    lines.append("")
    lines.append("## CSS Class Prefixes")
    lines.append("")
    for prefix, count in sorted(prefixes.items(), key=lambda item: (-item[1], item[0])):
        lines.append(f"- `{prefix}-*`: {count}")
    lines.append("")
    lines.append("## Most Common CSS Classes In Stylesheet")
    lines.append("")
    for cls, count in css_class_counts.most_common(60):
        lines.append(f"- `.{cls}`: {count} selector references")
    lines.append("")
    lines.append("## Most Common Inline/Page-Local CSS Classes")
    lines.append("")
    for cls, count in inline_class_counts.most_common(80):
        lines.append(f"- `.{cls}`: {count} selector references")
    lines.append("")
    lines.append("## Body Classes Seen")
    lines.append("")
    for cls, count in body_classes.most_common():
        lines.append(f"- `.{cls}`: {count} sampled pages")
    lines.append("")
    lines.append("## Sampled Pages")
    lines.append("")
    for path, status, parser in page_results:
        lines.append(f"### `{path}`")
        lines.append(f"- Status: HTTP {status}")
        lines.append(f"- Title: {parser.title or '(untitled)'}")
        if parser.body_class:
            lines.append(f"- Body class: `{parser.body_class}`")
        if parser.headings:
            lines.append(f"- Headings: {', '.join(parser.headings[:6])}")
        if parser.forms:
            lines.append(f"- Forms: {', '.join(parser.forms[:6])}")
        if parser.classes:
            top_classes = ", ".join(f"`.{cls}` ({count})" for cls, count in parser.classes.most_common(18))
            lines.append(f"- Frequent classes: {top_classes}")
        if inline_selectors_by_page[path]:
            lines.append(f"- Inline/page-local selectors: {len(inline_selectors_by_page[path])}")
        lines.append("")
    lines.append("## CSS Selectors")
    lines.append("")
    for selector in selectors:
        lines.append(f"- `{selector}`")
    with open("hub-public-style-snapshot.md", "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lines))
    print("Wrote hub-public-style-snapshot.md")
    return 0


if __name__ == "__main__":
    sys.exit(main())
