package quill.template;

import quill.info.Tag;
import quill.local.LocalPackage;

public class QuillTemplate {

	public static final QuillTemplate INSTANCE = new QuillTemplate();

	public static void main(LocalPackage lp, String[] args) throws Exception {
		System.out.println("Hello " + Tag.of(lp));
	}
}
