package de.axone.webtemplate.list;

public class DefaultPager_Form extends DefaultPager {
	
	/*override*/ {
		leftContainer = "<form class=\"pager\">";
		rightContainer = "</form>";
		
		leftTemplate = new StringTemplate( "<button name=\"link\" value=\"__link__\">&lt;&lt;</button>" );
		selectedLeftTemplate = new StringTemplate( "<button class=\"active\">&lt;&lt;</button>" );
		rightTemplate = new StringTemplate( "<button name=\"link\" value=\"__link__\">&gt;&gt;</button>" );
		selectedRightTemplate = new StringTemplate( "<button class=\"active\">&gt;&gt;</button>" );
		innerTemplate = new StringTemplate( "<button name=\"link\" value=\"__link__\">__no__</button>" );
		selectedTemplate = new StringTemplate( "<button class=\"active\">[__no__]</button>" );
		skippedTemplate = new StringTemplate( "&hellip;" );
		spaceTemplate = new StringTemplate( "&nbsp;" );
	}
	
	public DefaultPager_Form() {
		super();
	}

	public DefaultPager_Form( String nameBase, int selectedPage, int numPages ) {
		super( nameBase, selectedPage, numPages );
	}

}
