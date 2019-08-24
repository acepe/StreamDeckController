package de.acepe.streamdeck.backend.config.json;

import com.google.gson.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

public class ImageAdapter implements JsonSerializer<Image>, JsonDeserializer<Image> {

    @Override
    public JsonElement serialize(Image image, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", bos);
            byte[] imageBytes = bos.toByteArray();

            String imageString = Base64.getEncoder().encodeToString(imageBytes);
            obj.addProperty("imageData", imageString);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    public Image deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Image image = null;
        try {
            String jsonString = json.getAsJsonObject().get("imageData").getAsString();
            byte[] imageBytes = Base64.getDecoder().decode(jsonString);

            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            image = new Image(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
