package dogapi;

import java.util.List;

public interface BreedFetcher {

    List<String> getSubBreeds(String breed) throws BreedNotFoundException;

    // Make it a *checked* exception and explicitly extend the JDK's Exception.
    // Keeping the String ctor exactly as the test uses.
    class BreedNotFoundException extends java.lang.Exception {
        public BreedNotFoundException(String breed) {
            super("Breed not found: " + breed);
        }
        public BreedNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
