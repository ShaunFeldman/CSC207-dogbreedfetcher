package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        try {
            if (breed == null) {
                throw new BreedNotFoundException("Breed is null");
            }
            final String normalized = breed.trim().toLowerCase(Locale.ROOT);
            if (normalized.isEmpty()) {
                throw new BreedNotFoundException("Breed is empty");
            }

            final String url = "https://dog.ceo/api/breed/" + normalized + "/list";
            Request request = new Request.Builder().url(url).get().build();

            try (Response response = client.newCall(request).execute()) {
                if (response == null || !response.isSuccessful() || response.body() == null) {
                    throw new BreedNotFoundException(
                            "Failed to fetch sub-breeds for '" + breed + "'");
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);

                // Expect: {"message":[...], "status":"success"} for existing breeds
                if (!"success".equals(json.optString("status", ""))) {
                    // e.g. {"status":"error","message":"Breed not found (main breed does not exist)","code":404}
                    throw new BreedNotFoundException("Breed not found: '" + breed + "'");
                }

                JSONArray message = json.optJSONArray("message");
                if (message == null) {
                    throw new BreedNotFoundException(
                            "Unexpected API response format for '" + breed + "'");
                }

                List<String> subBreeds = new ArrayList<>(message.length());
                for (int i = 0; i < message.length(); i++) {
                    subBreeds.add(message.getString(i));
                }
                return subBreeds;
            }
        } catch (IOException | org.json.JSONException e) {
            // Per spec: report all failures as BreedNotFoundException
            throw new BreedNotFoundException(
                    "Failed to fetch sub-breeds for '" + breed + "'", e);
        }
    }
}