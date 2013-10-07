package de.axone.webtemplate.list;

public class DefaultPager_Form extends DefaultPager {
	
	/*override*/ {
		leftContainer = "<form class=\"pager\">";
		rightContainer = "</form>";
		
		leftTemplate = new Template( "<button name=\"link\" value=\"__link__\">&lt;&lt;</button>" );
		selectedLeftTemplate = new Template( "<button class=\"active\">&lt;&lt;</button>" );
		rightTemplate = new Template( "<button name=\"link\" value=\"__link__\">&gt;&gt;</button>" );
		selectedRightTemplate = new Template( "<button class=\"active\">&gt;&gt;</button>" );
		innerTemplate = new Template( "<button name=\"link\" value=\"__link__\">__no__</button>" );
		selectedTemplate = new Template( "<button class=\"active\">[__no__]</button>" );
		skippedTemplate = new Template( "&hellip;" );
		spaceTemplate = new Template( "&nbsp;" );
	}
	
	public DefaultPager_Form() {
		super();
	}

	public DefaultPager_Form( String nameBase, int selectedPage, int numPages ) {
		super( nameBase, selectedPage, numPages );
	}

}
