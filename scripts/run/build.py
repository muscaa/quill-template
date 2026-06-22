import zipfile

import __about__ as a
from scripts.files import SRC, BUILD, BUILD_GENERATED, VENV_LIB_SITEPACKAGES
from scripts import generate

def run():
    generate()

    BUILD.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(BUILD / f"{a.ID}-bundle.zip", "w", zipfile.ZIP_DEFLATED) as zip:
        for f in SRC.rglob("*"):
            if f.is_file():
                zip.write(f, f.relative_to(SRC))
        for f in BUILD_GENERATED.rglob("*"):
            if f.is_file():
                zip.write(f, f.relative_to(BUILD_GENERATED))
        for lib in VENV_LIB_SITEPACKAGES.iterdir():
            if lib.is_dir():
                if lib.name == "__pycache__" or lib.name.endswith(".dist-info"):
                    continue
                for f in lib.rglob("*"):
                    if f.is_file():
                        zip.write(f, f.relative_to(VENV_LIB_SITEPACKAGES))
            else:
                if lib.name == "_virtualenv.py" or lib.name.endswith(".pth"):
                    continue
                zip.write(lib, lib.relative_to(VENV_LIB_SITEPACKAGES))
            