from pathlib import Path

import scripts

PROJECT_ROOT = Path(scripts.__file__).parent.parent
VENV_LIB = PROJECT_ROOT / ".venv" / "lib"
VENV_LIB_SITEPACKAGES = next(p for p in Path(VENV_LIB).iterdir() if p.is_dir()) / "site-packages"
SRC = PROJECT_ROOT / "src" / "main" / "resources" / "bundle"
SRC_QUILL_HOME = SRC / "_"
SRC_QUILL_PACKAGE = SRC
BUILD = PROJECT_ROOT / "build"
BUILD_GENERATED = BUILD / "quill" / "generated"
BUILD_PREBUNDLE = BUILD / "quill" / "pre-bundle"
