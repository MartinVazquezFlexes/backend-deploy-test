package com.techforb.apiportalrecruiting.core.security.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {
    public String registerUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        return userRecord.getUid();
    }

    public void verifyToken(String idToken) throws FirebaseAuthException {
        FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public void changeUserPassword(String email, String newPassword) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userRecord.getUid())
                .setPassword(newPassword);

        FirebaseAuth.getInstance().updateUser(request);
    }
}
