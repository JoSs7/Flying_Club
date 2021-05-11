package clubVuelo;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Ingreso")
public class Ingreso extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		try {
			//Recuperamos los parámetros de la sesión
			HttpSession sesion = request.getSession(true);
			
			out.println("<html>");
			out.println("<head><title>Ingreso al club de vuelo</title></head>");
			out.println("<body>");
			out.println("<h2>Solicitud de ingreso al club de vuelo</h2>");
			out.println("<h3>Datos del solicitante:</h3>");
			out.println("Nombre: "+sesion.getAttribute("nombre")+"<br>");
			out.println("Contraseña: "+sesion.getAttribute("pass")+"<br>");
			out.println("Fecha de nacimiento: "+sesion.getAttribute("date")+"<br>");
			out.println("Nivel de vuelo: "+sesion.getAttribute("nivel")+"<br>");
			out.println("Vuelos practicados: "+sesion.getAttribute("vuelos")+"<br>");
			out.println("Idioma: "+sesion.getAttribute("idioma")+"<br>");
			if (!sesion.getAttribute("file").equals("")) {	//Compruebo si el campo foto está vacío
				out.println("Foto: "+sesion.getAttribute("file")+"<br>");
			} else {
				out.println("Foto: Sin foto de perfil");
			}
			out.println("<h4>Resultados de la evaluación de requisitos:</h4>");
			out.println("</body>");
			out.println("</html>");
			
			//Requisitos del club
			String condiciones = "";	//Cadena de condiciones
			
			//Comprobamos requisitos (Ver métodos abajo)
			if (comprobarNivel(sesion.getAttribute("nivel")) == false) {
				condiciones += "El nivel de vuelo mínimo requerido es Aficionado <br>";
			}
			
			if (comprobarVuelos(sesion.getAttribute("vuelos")) == false) {
				condiciones += "Tienes que practicar al menos dos tipos de vuelo <br>";
			}
			
			if (comprobarEdad(sesion.getAttribute("date")) == false) {
				condiciones += "No eres mayor de edad <br>";
			}
			
			//Vemos si se han cumplido todos los requisitos comprobando si la cadena está vacía
			if(condiciones.length() != 0) {
				out.println("<h3 style='color:red'>No apto</h3><h4>Requisitos no complidos:</h4><p style='color:red'>"+condiciones+"</p>");
			} else {
				out.println("<h3 style='color:green'>Apto</h3><h4 style='color:blue'>Enhorabuena!<p style='color:blue'>Ahora formas parte del club de vuelo! Bienvenido/a!</p></h4>");
			}
						
		}
    	catch (Exception e){
            out.println("Se produce una excepción <br>");
            out.println(e.getMessage());
    	}
	}	
	
	//Comprobar nivel
	protected boolean comprobarNivel(Object _nivel) {
		boolean apto = false;
		if (!_nivel.equals("Principiante")) {
			apto = true;
		}
		return apto;
	}
	
	//Comprobar cuántos tipos de vuelo hemos seleccionado
	protected boolean comprobarVuelos(Object _vuelos) {
		boolean apto = false;
		String convertedToString = String.valueOf(_vuelos);	 //Convertimos el objeto recibido de la sesión a String
		String[] array = convertedToString.split(" ");    //Separamos el String en un array por espacios
		
		if (array.length > 1) {	   //Si hay más de un elemento en el array
			apto = true;
		}
		return apto;
	}
	
	//Comprobamos si es mayor de edad
	protected boolean comprobarEdad(Object _date) {
		boolean apto = false;
		String convertedToString = String.valueOf(_date);	//Convertimos el objeto recibido de la sesión a String
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaActual = new Date();
		String cadena = formatoFecha.format(fechaActual);
		
		String[] fechaNac = convertedToString.split("/");
		String[] fechaAct = cadena.split("/");
		int anios = Integer.parseInt(fechaAct[2]) - Integer.parseInt(fechaNac[2]);
		int mes = Integer.parseInt(fechaAct[1]) - Integer.parseInt(fechaNac[1]);
		
		if (mes < 0) {
			anios = anios -1;
		}else if (mes == 0){
			int dia = Integer.parseInt(fechaAct[0]) - Integer.parseInt(fechaNac[0]);
			if (dia < 0) {
				anios = anios -1;
			}
		}		
		if (anios > 18) {
			apto = true;
		}
		return apto;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
