package com.tksimeji.wobject.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tksimeji.wobject.Wobject;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public final class ResourceUtility {
    private ResourceUtility() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull File getResource(@NotNull String path) {
        return new File(Wobject.plugin().getDataFolder(), path);
    }

    public static @NotNull JsonElement getJsonResource(@NotNull String path) {
        try {
            return JsonParser.parseReader(new FileReader(getResource(path)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setJsonResource(@NotNull String path, @NotNull JsonElement resource) {
        setJsonResource(getResource(path), resource);
    }

    public static void setJsonResource(@NotNull File file, @NotNull JsonElement resource) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(new GsonBuilder().setPrettyPrinting().setPrettyPrinting().create().toJson(resource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void newResource(@NotNull String path) {
        newResource(path, false);
    }

    public static void newResource(@NotNull String path, boolean replace) {
        URL url = Wobject.plugin().getClass().getClassLoader().getResource(path);

        if (url == null) {
            throw new IllegalArgumentException();
        }

        File resource = getResource(path);

        if (resource.exists() && ! replace) {
            return;
        }

        if (! resource.exists()) {
            File parent = resource.getParentFile();

            try {
                if ((parent != null && ! parent.mkdirs()) || ! resource.createNewFile()) {
                    throw new IllegalStateException();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream(resource);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.close();
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
