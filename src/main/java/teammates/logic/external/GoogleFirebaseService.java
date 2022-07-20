package teammates.logic.external;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import teammates.common.exception.FirebaseException;
import teammates.common.util.Logger;

/**
 * Google-based Firebase Admin SDK service.
 *
 * @see <a href="https://firebase.google.com/docs/reference/admin">Firebase Admin SDK</a>
 */
public class GoogleFirebaseService implements FirebaseService {

    private static final Logger log = Logger.getLogger();

    public GoogleFirebaseService(String serviceAccountFilename) {
        try {
            FileInputStream serviceAccount = new FileInputStream(serviceAccountFilename);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.getApplicationDefault())
//                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Initialized FirebaseApp instance of name " + FirebaseApp.getInstance().getName());
        } catch (FileNotFoundException | SecurityException e) {
            log.severe("File cannot be read.");
        } catch (IOException e) {
            log.severe("Google credentials cannot be created.");
        } catch (IllegalStateException e) {
            log.severe("The default FirebaseApp has already been initialized.");
        }
    }

    @Override
    public String generateLoginLink(String userEmail, String continueUrl) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(continueUrl)
                .setHandleCodeInApp(true)
                .build();
        try {
            return FirebaseAuth.getInstance().generateSignInWithEmailLink(userEmail, actionCodeSettings);
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            return null;
        }
    }

    @Override
    public void deleteUser(String userEmail) throws FirebaseException {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userEmail);
            FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            throw new FirebaseException(e);
        }
    }

}
