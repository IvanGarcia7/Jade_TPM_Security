import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String [] args) throws InterruptedException {

        //Ejecución del método para generar números aleatorios
        System.out.println("Por favor ingrese un número para generar n valores aleatorios:");
        Scanner sc = new Scanner(System.in);
        int valores = sc.nextInt();
        sc.close();

        ArrayList<String> numeros = MetodosTPM.numeros_aleatorios(valores);
        for(int i=0;i<numeros.size();i++){
            System.out.println(numeros.get(i));
        }

        //Ejecución para probar el método de escrcibir dado un string en un archivo en formato txt
        MetodosTPM.tpm2_crear_archivo_informacion("hola ivan\nque tal", "/home/pi/Desktop/testt/ajja/salida.txt");

        //Ejecucion para probar la creación tanto del key.pair como de la clave pública en cada agente
        String ruta = "/home/pi/Desktop/testt/ajja";
        MetodosTPM.tpm2_generar_claves2(ruta);

        //Ejecución para probar la lectura de archivos
        String content = MetodosTPM.tpm2_mostrar_contenido("/home/pi/Desktop/testt/ajja/salida.txt");
        System.out.println(content);
       

    }
}