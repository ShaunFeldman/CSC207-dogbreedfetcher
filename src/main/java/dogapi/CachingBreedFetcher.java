package dogapi;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // Build a case-insensitive cache key; still pass the original arg to the delegate.
        final String key = (breed == null) ? null : breed.trim().toLowerCase(Locale.ROOT);

        // Cache hit
        if (key != null && cache.containsKey(key)) {
            return cache.get(key);
        }

        // Cache miss → call delegate and record the attempt
        callsMade++;
        List<String> result = fetcher.getSubBreeds(breed); // may throw

        // Only cache successful results with a usable key
        if (key != null && !key.isEmpty()) {
            cache.put(key, result); // store the exact instance returned by delegate
        }
        return result;
    }

    public int getCallsMade() {
        return callsMade;
    }
}