package de.axone.webtemplate.tools;

import de.axone.webtemplate.form.FormParser.FormField;

public class FormDecoratorImpl implements FormDecorator {
	
	private static final String TEXT = 
			"A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath of that universal love which bears and sustains us, as it floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes, and heaven and earth seem to dwell in my soul and absorb its power, like the form of a beloved mistress, then I often think with longing, Oh, would I could describe these conceptions, could impress upon paper all that is living so full and warm within me, that it might be the mirror of my soul, as my soul is the mirror of the infinite God! O my friend -- but it is too much for my strength -- I sink under the weight of the splendour of these visions! A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath of that universal love which bears and sustains us, as it floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes, and heaven and earth seem to dwell in my soul and absorb its power, like the form of a beloved mistress, then I often think with longing, Oh, would I could describe these conceptions, could impress upon paper all that is living so full and warm within me, that it might be the mirror of my soul, as my soul is the mirror of the infinite God! O my friend -- but it is too much for my strength -- I sink under the weight of the splendour of these visions! A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of existence in this spot, which was created for the bliss of souls like mine. I am so happy, my dear friend, so absorbed in the exquisite sense of mere tranquil existence, that I neglect my talents. I should be incapable of drawing a single stroke at the present moment; and yet I feel that I never was a greater artist than now. When, while the lovely valley teems with vapour around me, and the meridian sun strikes the upper surface of the impenetrable foliage of my trees, and but a few stray gleams steal into the inner sanctuary, I throw myself down among the tall grass by the trickling stream; and, as I lie close to the earth, a thousand unknown plants are noticed by me: when I hear the buzz of the little world among the stalks, and grow familiar with the countless indescribable forms of the insects and flies, then I feel the presence of the Almighty, who formed us in his own image, and the breath of that universal love which bears and sustains us, as it floats around us in an eternity of bliss; and then, my friend, when darkness overspreads my eyes, and heaven and earth seem to dwell in my soul and absorb its power, like the form of a beloved mistress, then I often think with longing, Oh, would I could describe these conceptions, could impress upon paper all that is living so full and warm within me, that it might be the mirror of my soul, as my soul is the mirror of the infinite God! O my friend -- but it is too much for my strength -- I sink under the weight of the splendour of these visions! A wonderful serenity has taken possession of my entire soul, like these sweet mornings of spring which I enjoy with my whole heart. I am alone, and feel the charm of";

	public StringBuilder formatInput( FormField field ){
		
		StringBuilder result = new StringBuilder();
		
		result
			.append( "\t<div class=\"field " + field.formName() + "\">\n" )
			.append( "\t\t" ).append( Character.toUpperCase( field.pojoName().charAt( 0 ) ) + field.pojoName().substring( 1 ) )
			.append( ": " )
			.append( "__" )
			.append( field.formName() )
			.append( "__" )
			.append( '\n' )
			.append( "\t</div>\n" )
		;
		
		return result;
	}
	
	private CharSequence script( String path ){
		
		return "\t\t<script type=\"text/javascript\" src=\"" + path + "\"></script>\n";

	}
	private CharSequence css( String path ){
		
		return "\t\t<link href=\""+path+"\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" />\n";

		

	}
	
	private CharSequence blindtext( int length ){
		
		return TEXT.substring( 0, length );
	}
	
	public StringBuilder formatPage( CharSequence content ){
		
		StringBuilder result = new StringBuilder();
		
		result
			.append( 
				"@Class: de.axone.hvm.template.FormTemplate\n" +
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
				"\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
				"\n" +
				"<html>\n<head><title>Form</title>\n" )
				
			.append( script( "clientcide.2.2.0.js" ) )
			.append( script( "main.js" ) )
			.append( script( "lang/de.js" ) )
			.append( script( "formcheck.js" ) )
			.append( css( "theme/classic/formcheck.css" ) )
			
			.append( 
				"</head>\n" +
				"<body>\n" )
				
			.append( "<div id=\"abc\">" )
			.append( blindtext( 1000 ) ).append( '\n' )
			.append( "</div>\n" )
				
			.append( "<form id=\"input_form\">\n" )
		
			.append( content )
			
			.append( "<input type=\"submit\" name=\"submit\" />" )
			
			.append( "</form>" )
		
			.append( 
				"</body>\n" +
				"</html>\n" )
		;
		
		return result;
	}
}
