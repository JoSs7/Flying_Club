package clubVuelo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Solicitud")
public class Solicitud extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//Creamos el array asociativo para nivel de vuelo y tipos de vuelo
	private LinkedHashMap<String, String> arrayVuelos = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> arrayNivel = new LinkedHashMap<String, String>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        //Ingresamos los datos al array asociativo arrayNivel
        arrayNivel.put("PRI", "Principiante");
        arrayNivel.put("AF", "Aficionado");
        arrayNivel.put("VE", "Veterano");
        arrayNivel.put("PRO", "Profesional");
		
        //Ingresamos los datos al array asociativo arrayVuelos
        arrayVuelos.put("PA", "Parapente");
        arrayVuelos.put("AL", "Aladelta");
        arrayVuelos.put("PAR", "Paracaídas");

        // Recepción de parámetros de la sesión
        String nombre = request.getParameter("nombre");
        String pass = request.getParameter("pass");
        String date = request.getParameter("date");
        String nivel = request.getParameter("nivel");
        String[] vuelos = request.getParameterValues("vuelos[]");
        String idioma = request.getParameter("idioma");
        String file = request.getParameter("file");
        String aceptar = request.getParameter("aceptar");
        
        
        //Creación del formulario vacío inicial
        if (nombre == null) {   //Primera llamada 	
            out.println("<html>");
            out.println("<head><title>Solicitud de ingreso al club de vuelo</title></head>");
            out.println("<body>");
        	out.println("<h3>Formulario de ingreso al club de vuelo</h3>");
        	out.println("<form name='formulario' action='Solicitud' method='post'>");
        	out.println(generaNombre(nombre));
        	out.println(generaPass(pass));
        	out.println(generaDate(date));
        	out.println(generaNivel(arrayNivel, nivel));
        	out.println(generaVuelos(arrayVuelos, vuelos));
        	out.println(generaRadioIdioma(idioma));
        	out.println("<label for='file'>Foto </label><input type='file' name='file'><br><br>");
        	out.println(generaCheck(aceptar));
        	out.println("<input type='submit' value='Solicitar ingreso al club'>");
        	out.println("</form>");
        	
        } else {
        	
            //Lógica de negocio
            String errores = ""; //Cadena de errores
            
            if (nombre == "") {
                errores += "Debes rellenar el nombre <br>";
            }
            
            if (pass == "") {
            	errores += "Debes rellenar la contraseña <br>";
            }
            
            if (pass != "" && pass.length() < 5) {
            	errores += "La contraseña debe tener una longitud mayor que 5 <br>";
            }
            
            if (pass != "" && pass.length() > 12) {
            	errores += "La contraseña debe tener una longitud menor que 12 <br>";
            }
            
            if (date == "" || date == null) {	//Comprobamos si la fecha está vacía
            	errores += "Debes indicar tu fecha de nacimiento <br>";    
            	
            } else if (Pattern.matches("\\d{1,2}/\\d{1,2}/\\d{4}", date) == false){    //Comprobamos el formato dd/mm/aaaa
            	errores += "La fecha debe estar en un formato dd/mm/aaaa <br>";
            	
            } else if (Pattern.matches("(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[0-2])/([12][09]\\d{2})", date) == false){    //Comprobamos que sea un fecha válida
                	errores += "Debes poner una fecha válida <br>";
            }
               
            if (nivel == null) {
            	errores += "Debes elegir un nivel de vuelo <br>";
            }
            
            if (vuelos == null) {
            	errores += "Debes elegir al menos un tipo de vuelo <br>";
            }
            
            if (idioma == null) {
            	errores += "Debes elegir un idioma <br>";
            }                  
            
            if (aceptar == null) {
            	errores += "Debes aceptar los términos y condiciones";
            }
                       
            
            //Comprobamos si hay errores en la cadena
            if (errores.length() != 0) {	//Si hay errores    	
                out.println("<html>");
                out.println("<head><title>Solicitud de ingreso al club de vuelo</title></head>");
                out.println("<body>");
            	out.println("<h3>Formulario de ingreso al club de vuelo</h3>");
                out.println("<p style=\"color:red\">" + errores + "</p>");
            	out.println("<form name='formulario' action='Solicitud' method='post'>");
            	out.println(generaNombre(nombre));
            	out.println(generaPass(pass));
            	out.println(generaDate(date));
            	out.println(generaNivel(arrayNivel, nivel));
            	out.println(generaVuelos(arrayVuelos, vuelos));
            	out.println(generaRadioIdioma(idioma));
            	out.println("<label for='file'>Foto </label><input type='file' name='file'><br><br>");
            	out.println(generaCheck(aceptar));
            	out.println("<input type='submit' value='Solicitar ingreso'>");
            	out.println("</form>");
            	out.println("</body>");          
            	out.println("</html>");
            	
            } else {	//Si no hay errores
            	
            	try {
            		
            		//Se vacía la sesión si existe
                    if (request.getSession(false) != null) {  //Si existe una sesión
                        request.getSession().invalidate();  //La eliminamos
                    }
            		
                    //Se crea una sesión
                    HttpSession sesion = request.getSession(true);
                	
                    //Pasamos los parámetros a la sesión
                    sesion.setAttribute("nombre", nombre);
                    sesion.setAttribute("pass", pass);
                    sesion.setAttribute("date", date);
                    sesion.setAttribute("nivel", convertirNivel(arrayNivel, nivel));	//Ver método convertirNivel
                    sesion.setAttribute("vuelos", convertirVuelos(arrayVuelos, vuelos));	//Ver método convertirVuelos
                    sesion.setAttribute("idioma", idioma);
                    sesion.setAttribute("file", file);
                    
                	response.sendRedirect("Ingreso");	//Redireccionamos al servlet Ingreso
            		
            	}
            	catch (Exception e){
                    out.println("Se produce una excepción <br>");
                    out.println(e.getMessage());
            	}
            }
        }
	}
	
	//SCRIPLETS
    
	//Genera nombre
    protected String generaNombre(String _nombre) {
        String cadena = "Nombre: ";
        if (_nombre == null) {
            cadena += "<input type='text' name='nombre'><br><br>";
        } else {
            cadena += "<input type='text' name='nombre' value="+_nombre+"><br><br>";
        }
        return cadena;
    }
    
    //Genera pass
    protected String generaPass(String _pass) {
    	String cadena = "Clave: ";
    	if (_pass == null) {
    		cadena += "<input type='pass' name='pass'><br><br>";
    	} else {
    		cadena += "<input type='pass' name='pass' value="+_pass+"><br><br>";
    	}
    	return cadena;
    }
    
    //Genera fecha
    protected String generaDate(String _date) {
    	String cadena = "Fecha de nacimiento: ";
    	if (_date == null) {
    		cadena += "<input type='text' name='date'><br><br>";
    	} else {
    		cadena += "<input type='text' name='date' value="+_date+"><br><br>";
    	}
    	return cadena;
    }
    
    //Genera selects nivel de vuelo
    protected String generaNivel(LinkedHashMap<String, String> arrayNivel, String _nivel) {
        String cadena = "Selecciona tu nivel de vuelo: <select name='nivel'>"
        		       +"<option selected='selected' disabled='disabled'>Nivel</option>";
        
        Iterator<String> iterador = arrayNivel.keySet().iterator();
        
        while (iterador.hasNext()) {
            String clave = (String)iterador.next();
            String valor = (String)arrayNivel.get(clave);   
            
            if (_nivel == null) {
            	cadena += "<option value="+clave+">"+valor+"</option>";
            } else if (_nivel.equals(clave)){
            		cadena += "<option value="+clave+" selected='selected'>"+valor+"</option>";
            } else {
            	cadena += "<option value="+clave+">"+valor+"</option>";
            }
        }
        cadena +="</select><br><br>";
        return cadena;
    }
    
    //Genera checkboxs tipo de vuelos
    protected String generaVuelos(LinkedHashMap<String, String> arrayVuelos,  String[] _vuelos) {
    	String cadena = "Elige los tipos de vuelo que practicas: (Puedes seleccionar más de uno) <br>";
    	int numVuelos = 0;
    	int i = 0;
    	
    	if (_vuelos != null) {
    		numVuelos = _vuelos.length;
    	}
    	Iterator<String> iterador = arrayVuelos.keySet().iterator();
    	
    	while (iterador.hasNext()) {
            String clave = (String)iterador.next();
            String valor = (String)arrayVuelos.get(clave);
            if ( i<numVuelos && _vuelos[i].equals(clave)) { 
            	cadena += "<input type='checkbox' name='vuelos[]' value="+clave+" checked>"+valor+"<br>";
            	i++;
            } else {
            	cadena += "<input type='checkbox' name='vuelos[]' value="+clave+">"+valor+"<br>";
            }
    	}
    	cadena += "<br>";
    	return cadena;
    }
    
    //Genera radios idioma
    protected String generaRadioIdioma(String _idioma) {
    	String cadena = "Elige un idioma: &nbsp;";
    	if (_idioma == null) {
    		cadena += "<label for='espanol'> Español</label><input type='radio' name='idioma' value='Espanol'>"
    				+ "<label for='ingles'> Inglés</label><input type='radio' name='idioma' value='Ingles'>";
    	} else if(_idioma == "espanol"){
    		cadena += "<label for='espanol'>Español</label><input type='radio' name='idioma' value='Espanol' checked>"
    				 +"<label for='ingles'>Inglés</label><input type='radio' name='idioma' value='Ingles'>";
    	} else {
    		cadena += "<label for='espanol'>Español</label><input type='radio' name='idioma' value='Espanol'>"
   				 +"<label for='ingles'>Inglés</label><input type='radio' name='idioma' value='Ingles' checked>";
    	}
    	cadena += "<br><br>";
    	return cadena;
    }
    
    //Genera checbox para aceptar términos y condiciones
    protected String generaCheck(String _aceptar) {
    	String cadena = "<label for='aceptar'>Acepto los términos y condiciones </label>";
    	if(_aceptar == null) {
    		cadena += "<input type='checkbox' name='aceptar'>";
    	} else {
    		cadena += "<input type='checkbox' name='aceptar' checked>";
    	}
    	cadena += "<br><br>";
    	return cadena;
    }
    
    //MÉTODOS convertirNivel y convertirVuelos
    //Lo he hecho así porque tenía problemas para recuperar de la sesión un array, ya que luego se recupera como un objeto
    
    //Convierto la clave del nivel en su valor del array para mandarlo en la sesión
	protected String convertirNivel (LinkedHashMap<String, String> arrayNivel, String _nivel) {
		String cadena = "";
        Iterator<String> iterador = arrayNivel.keySet().iterator();
        
        while (iterador.hasNext()) {
            String clave = (String)iterador.next();
            String valor = (String)arrayNivel.get(clave);   
            
            if (_nivel.equals(clave)){
            		cadena += valor;
            }
        }
		return cadena;
	}
    
    //Convierto el array vuelos a un String para pasarlo a la sesión y luego poder recuperarlo para mostrarlo
	//Al ennviarse un array a la sesión, no podía recuperarlo bien para mostrar los datos, ya que se envía como objeto
    protected String convertirVuelos(LinkedHashMap<String, String> arrayVuelos, String[] _vuelos) {
    	String cadena = "";
    	int i = 0;
        Iterator<String> iterador = arrayVuelos.keySet().iterator();
            
        while (iterador.hasNext()) {
            String clave = (String)iterador.next();
            String valor = (String)arrayVuelos.get(clave);   
                
            if (i < _vuelos.length && _vuelos[i].equals(clave)){
                cadena += valor+ " ";
                i++;
            }
        }
        return cadena;
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
