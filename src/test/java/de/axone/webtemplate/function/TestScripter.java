package de.axone.webtemplate.function;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

@Test( groups="webtemplates.function.scripter" )
public class TestScripter {
	
	@Test
	public void testScripter() throws Exception {
		
		Scripter scripter = new Scripter();
		
		assertEquals( scripter.run( "<b><?5+4?></b>" ), "<b></b>" );
		// 'print' not functioning
		//assertEquals( scripter.run( "<b><?print(5+4)?></b>" ), "<b>9</b>" );
		assertEquals( scripter.run( "<b><?=5+4?></b>" ), "<b>9</b>" );
		assertEquals( scripter.run( "<b><?='5+4'?></b>" ), "<b>5+4</b>" );
		assertEquals( scripter.run( "<?var a=5+4?><b><?=a?></b>" ), "<b>9</b>" );
		
	}
}
