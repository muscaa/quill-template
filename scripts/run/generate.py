import shutil
import json

import __about__ as a
from scripts.files import BUILD_GENERATED

def run():
    shutil.rmtree(BUILD_GENERATED, ignore_errors=True)
    BUILD_GENERATED.mkdir(parents=True, exist_ok=True)

    package_json = BUILD_GENERATED / "package.json"
    package_json.write_text(json.dumps({
        "id": a.ID,
        "author": a.AUTHOR_ID,
        "version": a.VERSION,
        "description": a.DESCRIPTION,
    }, indent=4))
