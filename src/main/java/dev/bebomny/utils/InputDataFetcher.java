package dev.bebomny.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Collectors;

public class InputDataFetcher {

    private static final String AOC_URL = "https://adventofcode.com/";
    private static final String AOC_INPUT_ENDPOINT = "2023/day/{day}/input";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";

    //Control variables - change via refetch() and dontFetch() they're static so call them anywhere in the days class
    // before the constructor gets executed
    public static boolean refetch = false;
    public static boolean dontFetch = false;

    //It's only going to fetch data if the file is empty or missing
    // so if you need an empty file use dontFetch()
    // IF you need to refetch the data but the file already exists call refetch()

    public static void fetchAndSave(String day) {

        if (dontFetch) //return here if dontFetch was set
            return;

        URL url = constructURL(Integer.parseInt(day));
        Path inputDataPath = Path.of(AoCUtils.RESOURCE_PATH + "Day" + day + ".txt");

        if (Files.exists(inputDataPath) && !refetch) //return here if the file already exists and refetch is not set
            return;

        try {
            if (!Files.exists(inputDataPath)) Files.createFile(inputDataPath);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Cookie", "session=" + getSessionKey());

            //Read and save - it writes an invisible character at the end, so I don't know If it'll work well,
            // but everything else seems fine
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            BufferedWriter bufferedWriter = Files.newBufferedWriter(inputDataPath);

            int read;
            while ((read = bufferedInputStream.read()) > 0) {
                bufferedWriter.write(read);
            }

            bufferedInputStream.close();
            bufferedWriter.close();
        } catch (IOException exception) {
            throw new QuackYouException("Something went wrong when fetching day " + day + " input data!", exception);
        }
    }

    //Constructs a URL for a given day
    //I'm converting day into int here to prevent some edge cases where day could be '02' instead of '2'
    // so it reaches the correct endpoint
    private static URL constructURL(int day) {
        String url = AOC_URL + AOC_INPUT_ENDPOINT.replace("{day}", String.valueOf(day));
        System.out.println("Fetching from: " + url);

        URL constructedURL;
        try {
            constructedURL = URL.of(URI.create(url), null);
        } catch (MalformedURLException e) {
            throw new QuackYouException("Somehow this static URL is wrong.");
        }

        return constructedURL;
    }

    //Specified in .env file in the root directory
    //this file is added in .gitignore, to be not shared online
    //It should contain one line looking like this:
    //session=YOUR_SESSION_COOKIE
    //You can get this value from your browser using the network tab in developer options
    //when looking at the request headers under "Cookie"
    private static String getSessionKey() {
        Path dotenvPath = Path.of(".env");
        HashMap<String, String> dotenvVariables;

        if (!Files.exists(dotenvPath))
            throw new QuackYouException("Couldn't find any dotenv files. Please create one in the root directory!");

        try (BufferedReader reader = Files.newBufferedReader(dotenvPath)) {
            dotenvVariables = new HashMap<>(reader.lines().collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1])));

        } catch (IOException exception) {
            throw new QuackYouException("Unable to read dotenv file!", exception);
        }

        return dotenvVariables.get("session");
    }
}
