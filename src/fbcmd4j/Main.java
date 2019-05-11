package fbcmd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

public class Main {
	static final Logger logger = LogManager.getLogger(Main.class);

	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";
	private static int seleccion = 0;

	public static void main(String[] args) {
		logger.info("Iniciando app");
		Facebook fB = null;
		Properties props = null;

		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
		} catch (IOException ex) {
			logger.error(ex);
		}

		try {
			Scanner scanner = new Scanner(System.in);
			while (true) {
				//Menu de FBCMD4J
				fB = Utils.configFacebook(props);
				System.out.println("FBCMD4J Programa para uso del cliente: ");
				System.out.println("1. obtener_newsfeed");
				System.out.println("2. obtener_wall");
				System.out.println("3. publicar_estado");
				System.out.println("4. publicar_link");
				System.out.println("5. Cerrar el programa:");
				System.out.println("Ingrese su opcion: ");

				try {
					seleccion = scanner.nextInt();
					scanner.nextLine();
					switch (seleccion) {
					case 1:
						System.out.println("Obtener el NewsFeed");
						ResponseList<Post> newsFeed = fB.getFeed();
						for (Post p : newsFeed) {
							Utils.printPost(p);
						}
						guardar_Facebook("NewsFeed", newsFeed, scanner);
						break;
					case 2:
						System.out.println("Obtener Muro");
						ResponseList<Post> wall = fB.getPosts();
						for (Post p : wall) {
							Utils.printPost(p);
						}
						guardar_Facebook("Wall", wall, scanner);
						break;
					case 3:
						System.out.println("Publicar Estado");
						String estado = scanner.nextLine();
						Utils.postStatus(estado, fB);
						break;
					case 4:
						System.out.println("Publicar Link");
						String link = scanner.nextLine();
						Utils.postLink(link, fB);
						break;
					case 5:
						System.out.println("Programa terminado.");
						System.exit(0);
						break;
					default:
						logger.error("Opcion invalida");
						break;
					}
				} catch (InputMismatchException IME) {
					System.out.println("Error, revisar log.");
					logger.error("Opción invalida.", IME.getClass());
				} catch (FacebookException FE) {
					System.out.println("Error, revisar log.");
					logger.error(FE.getErrorMessage());
				} catch (Exception E) {
					System.out.println("Error, revisar log.");
					logger.error(E);
				}
				System.out.println();
			}
		} catch (Exception EX) {
			logger.error(EX);
		}
	}
	//Clase para guardar el registro de las acciones realizadas en FBCMD4J
	public static void guardar_Facebook(String fileName, ResponseList<Post> posts, Scanner scanner) {
		System.out.println("¿Quieres guardar lo mostrado en un archivo txt?");
		String seleccion = scanner.nextLine();
		if (seleccion.contains("Si") || seleccion.contains("si") || seleccion.contains("Yes") || seleccion.contains("yes")) {
			List<Post> post = new ArrayList<>();
			int num = 0;
			while (num <= 0) {
				try {
					System.out.println("¿Cuantas lineas quieres guaradar?");
					num = Integer.parseInt(scanner.nextLine());
					if (num <= 0) {
						System.out.println("Error: el numero tiene que ser mayor de 0.");
					} else {
						for (int i = 0; i < num; i++) {
							if (i > posts.size() - 1)
								break;
							post.add(posts.get(i));
						}
					}
				} catch (NumberFormatException e) {
					logger.error(e);
				}
			}
			Utils.Save_Post(fileName, post);
		}
	}
}