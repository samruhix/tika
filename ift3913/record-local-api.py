"""Local-only HTTP recorder: preserves ChatUniTest requests and Ollama responses."""
import itertools
import json
from pathlib import Path
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.request import Request, urlopen
from urllib.error import HTTPError

OUTPUT = Path(__file__).parent / "artifacts" / "api-raw"
OUTPUT.mkdir(parents=True, exist_ok=True)
COUNTER = itertools.count(1)


class Recorder(BaseHTTPRequestHandler):
    def do_POST(self):
        number = next(COUNTER)
        payload = self.rfile.read(int(self.headers["Content-Length"]))
        (OUTPUT / f"{number:04d}-request.json").write_bytes(payload)
        request = Request("http://127.0.0.1:11434" + self.path, data=payload,
                          headers={"Content-Type": "application/json"})
        try:
            with urlopen(request, timeout=600) as response:
                body, status = response.read(), response.status
        except HTTPError as error:
            body, status = error.read(), error.code
        (OUTPUT / f"{number:04d}-response.json").write_bytes(body)
        (OUTPUT / f"{number:04d}-status.json").write_text(
            json.dumps({"status": status, "path": self.path}), encoding="utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    ThreadingHTTPServer(("127.0.0.1", 11435), Recorder).serve_forever()
