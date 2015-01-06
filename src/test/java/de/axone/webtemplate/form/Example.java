package de.axone.webtemplate.form;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.E;
import de.axone.tools.Str;
import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.AbstractFileWebTemplate;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.WebTemplate;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.element.FormValueFactoryHeadshop;
import de.axone.webtemplate.element.FormValueFactory;

public class Example {
	
	//@Test( groups="webtemplate.example" )
	public void runExample() throws Exception {
		
		TestHttpServletRequest request = new TestHttpServletRequest();
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		request.setParameter( "action", "save" );
		request.setParameter( "username", "flo" );
		request.setParameter( "email", "f.bantner.axon-e.de" );
		request.setParameter( "age", "33" );
		
		doGet( request, response );
	}
	
	public void doGet( HttpServletRequest request, HttpServletResponse response ) 
		throws Exception {
		
		try {
			
			// Get WebTemplate. In reality would use Factory
			WebTemplate template = new ExamplePage();
			
			// Instantiate empty form
			ExampleForm form = new ExampleForm();
			
			// Determine what to do
			String action = request.getParameter( "action" );
			
			//Note: prevent NPE
			if( "save".equals( action ) ){
				
				form.readFromRequest( request );
				
				if( form.isValid() ){
					
					String username = form.getUsername();
					String email = form.getEmail();
					Integer age = form.getAge();
					
					E.rr( username, email, age );
					
					// DO WHATEVER NEEDS TO BE DONE
					
					response.sendRedirect( "/someotherpage" );
					return;
				} else {
					
					List<String> failures = form.validate();
					
					template.setParameter( "message", Str.join( ", ", failures ) );
					
				}
			}
			
			// Give the form to the template.
			// Note that this is one way of doing it. 
			// Setting parameters or accessing it externally
			// would be fine to by now.
			template.render( form, response.getWriter(), request, response, null, null );
			
			// This is only for this example to show the output
			E.rr( ((TestHttpServletResponse)response).getContent() );
			
		} catch( Exception e ) {
			
			// Here would be exception handling. 
			// e.g. sending http error responses.
			// for this example we throw it further.
			
			throw( e );
		}
	}
	
	private static final String USERNAME = "username";
	private static final String EMAIL = "email";
	private static final String AGE = "age";
		
	public static class ExampleForm extends WebFormImpl {
		
		private FormValue<String> username;
		private FormValue<String> email;
		private FormValue<Integer> age;
		
		public ExampleForm() throws WebTemplateException {
			
			FormValueFactory fvf = new FormValueFactoryHeadshop();
			
			username = fvf.createInputTextValue( USERNAME, 16, false );
			this.addFormValue( USERNAME, username );
			
			email = fvf.createInputEMailValue( EMAIL, 255, false );
			this.addFormValue( EMAIL, email );
			
			age = fvf.createInputIntegerValue( AGE, Locale.GERMANY, Integer.MIN_VALUE, Integer.MAX_VALUE, false );
			this.addFormValue( AGE, age );
		}
		
		public String getUsername() throws ConverterException{
			return username.getValue();
		}
		public String getEmail() throws ConverterException{
			return email.getValue();
		}
		public Integer getAge() throws ConverterException{
			return age.getValue();
		}
		
		public void setUsername( String username ) throws ConverterException{
			this.username.setValue( username );
		}
		public void setEmail( String email ) throws ConverterException{
			this.email.setValue( email );
		}
		public void setAge( Integer age ) throws ConverterException{
			this.age.setValue( age );
		}
	}
	
	private static class ExamplePage extends AbstractFileWebTemplate {

		public ExamplePage() throws Exception {
			super( new File( "src/test/de/axone/webtemplate/form/example.xhtml" ) );
		}

		@Override
		public void render( Object object , PrintWriter out , HttpServletRequest request ,
				HttpServletResponse response , Translator translator , ContentCache cache  ) throws IOException,
				WebTemplateException, Exception {
			
			WebForm form = (WebForm) object;
			
			DataHolder h = getHolder();

			h.setValue( "input_username", form.getHtmlInput( USERNAME ) );
			h.setValue( "input_email", form.getHtmlInput( EMAIL ) );
			h.setValue( "input_age", form.getHtmlInput( AGE ) );
			
			h.render( object, out, request, response, translator, cache );
		}
	}
	
}
