import os

ID = "quill-template-java"
NAME = "Quill Template Java"
AUTHOR_ID = "muscaa"
AUTHOR_NAME = "musca"
VERSION = os.environ.get("GITHUB_REF_NAME", "v0.0.1-SNAPSHOT").removeprefix("v")
DESCRIPTION = "Quill java package template"
