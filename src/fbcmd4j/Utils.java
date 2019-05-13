package fbcmd4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.Post;

public class Utils {
	private static final Logger logger = LogManager.getLogger(Utils.class);

	// Propiedades del archivo de config
	public static Properties loadConfigFile(String folder_Name, String file_Name) throws IOException {
		Properties propiedades = new Properties();
		Path configFile = Paths.get(folder_Name, file_Name);
		propiedades.load(Files.newInputStream(configFile));
		BiConsumer<Object, Object> emptyProperty = (x, y) -> {
			if (((String) y).isEmpty())
				logger.info("Empty property '" + x + "' ");
		};
		propiedades.forEach(emptyProperty);
		return propiedades;
	}

	// Datos requeridos del archivo fbcmd4j.properties
	// oauth.accesstoken user access token: EAAIwWR8bZCV8BAGpRx415KS0mkQfrmSCKKj298v9i5jZANdPvbx9SQTbVqgTjSt2fkhuYHZAZBYV1L8tja7Jj5Z
	// AX8RZAfOkFuHSgCZBUOMw7mX1gS7tc1xAwCJWxgRASWWj8uSNepigj81G6ag7X1ry9mBUKZBOfNcWke5HQg0F5BDlNgGEvb2d2hZAZBqbe6ZCPEZD
	// oauth.accesstoken App Token:616109285571935|e9jXJ43OWX7hXpqAXbZemcSy7EU
	public static Facebook configFacebook(Properties propiedades) {
		Facebook facebook_instance = new FacebookFactory().getInstance();
		facebook_instance.setOAuthAppId(propiedades.getProperty("oauth.appId"),
				propiedades.getProperty("oauth.appSecret"));
		facebook_instance.setOAuthPermissions(propiedades.getProperty("oauth.permissions"));
		facebook_instance.setOAuthAccessToken(new AccessToken(propiedades.getProperty("oauth.accessToken"), null));
		return facebook_instance;
	}

	// Clase para poner/publicar posts
	public static void printPost(Post posts_) {
		if (posts_.getStory() != null) {
			System.out.println("Historia: \n" + posts_.getStory());
		}
		if (posts_.getMessage() != null) {
			System.out.println("Mensaje: \n" + posts_.getMessage());
		}
		System.out.println("...");
	}

	// Clase para poner/publicar links
	public static void postLink(String link, Facebook fb) {
		try {
			fb.postLink(new URL(link));
		} catch (MalformedURLException MURLE) {
			logger.error(MURLE);
		} catch (FacebookException FE) {
			logger.error(FE);
		}
	}

	// Clase para poner/publicar el estado de la cuenta del usuario
	public static void postStatus(String message, Facebook fb) {
		try {
			fb.postStatusMessage(message);
		} catch (FacebookException FE) {
			logger.error(FE);
		}
	}

	// Clase para guardar los datos que tenga almacenados el post
	public static String guardar_Post(String fileName, List<Post> posts) {
		File file = new File(fileName + ".txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream f = new FileOutputStream(file);

			for (Post p : posts) {
				String m = "";
				if (p.getStory() != null)
					m += "Historia: " + p.getStory() + "\n";
				if (p.getMessage() != null)
					m += "Mensaje: " + p.getMessage() + "\n" + "...\n";
				f.write(m.getBytes());
			}
			f.close();
			logger.info("Filepath name: '" + file.getName() + "'.");
			System.out.println("Filepath name '" + file.getName() + "'.");
		}

		catch (IOException IOE) {
			logger.error(IOE);
		}
		return file.getName();
	}
}