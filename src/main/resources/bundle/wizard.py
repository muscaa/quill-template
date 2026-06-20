from quill.setup.versions import V1

def install():
    v1 = V1(__file__)
    v1.copy("wizard.py")
    v1.copy("package.json")

    v1.bins([
        "_/bin/quill-template-java",
        "_/bin/quill-template-java.cmd",
    ])

    v1.copy("bin/")
    v1.copy("java/")

def uninstall():
    v1 = V1(__file__)
    v1.delete("wizard.py")
    v1.delete("package.json")

    v1.delete("@/bin/quill-template-java")
    v1.delete("@/bin/quill-template-java.cmd")

    v1.delete("bin/")
    v1.delete("java/")
