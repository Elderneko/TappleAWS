package com.example.cegoc.tapple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.security.KeyStore;

import javax.crypto.Cipher;

import cad.Teacher;

public class  LoginActivity extends AppCompatActivity {

    private EditText edt_user, edt_pass;
    private Intent change;
    private ProgressBar pb;
    private Button btn;

    private class BackTaskDB extends android.os.AsyncTask<Void, Teacher, Teacher> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
            btn.setEnabled(false);
        }

        @Override
        protected Teacher doInBackground(Void... voids) {
            cad.TappleCAD t = new cad.TappleCAD();
            int id_teacher = t.checkLogin(edt_user.getText().toString(), edt_pass.getText().toString());
            // Si la id es 0, no existe el usuario
            if(id_teacher != 0){
                return t.showProfile(id_teacher);
            } else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Teacher t) {
            super.onPostExecute(t);
            pb.setVisibility(View.GONE);
            btn.setEnabled(true);
            // Si no existe el usuario no se hace otra llamada a la BD
            if(t != null){
                // Iniciando sesion...
                saveInfo(t.getId_teacher(), t.getUser());
                startActivity(change);
                finishAffinity();
            } else {
                // Credenciales incorrectas
                Toast.makeText(LoginActivity.this,
                        "Credenciales incorrectos",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pb.setVisibility(View.GONE);
            btn.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pb = findViewById(R.id.pb_login);
        btn = findViewById(R.id.btn_login);
        change = new Intent(this, MainMenu.class);
        edt_user = findViewById(R.id.edt_user);
        edt_pass = findViewById(R.id.edt_pass);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackTaskDB().execute();
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //Get an instance of KeyguardManager and FingerprintManager//
//            keyguardManager =
//                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//            fingerprintManager =
//                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//
//            textView = (TextView) findViewById(R.id.edt_pass);
//
//            //Check whether the device has a fingerprint sensor//
//            if (!fingerprintManager.isHardwareDetected()) {
//                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
//                textView.setText("Your device doesn't support fingerprint authentication");
//            }
//            //Check whether the user has granted your app the USE_FINGERPRINT permission//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//                // If your app doesn't have this permission, then display the following text//
//                textView.setText("Please enable the fingerprint permission");
//            }
//
//            //Check that the lockscreen is secured//
//            if (!keyguardManager.isKeyguardSecure()) {
//                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
//                textView.setText("Please enable lockscreen security in your device's Settings");
//            } else {
//                try {
//                    generateKey();
//                } catch (FingerprintException e) {
//                    e.printStackTrace();
//                }
//
//                if (initCipher()) {
//                    //If the cipher is initialized successfully, then create a CryptoObject instance//
//                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
//
//                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
//                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
//                    FingerprintHandler helper = new FingerprintHandler(this);
//                    helper.startAuth(fingerprintManager, cryptoObject);
//                }
//            }
//        }
    }

    /**
     * Guarda en el shared preferences una id y un nombre
     *
     * @param id_param id a guardar
     * @param user nombre de usuario a guardar
     */
    private void saveInfo(int id_param, String user){
        SharedPreferences preferencias=getSharedPreferences("TEACHER_INFO",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("ID_TEACHER", id_param);
        editor.putString("USER_TEACHER", user);
        editor.apply();
    }

    /**
     * Lleva al formulario de registro
     *
     * @param v
     */
    public void goRegister(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * Lleva al olvidar contraseña
     *
     * @param v
     */
    public void goForgetPass(View v){
        startActivity(new Intent(this, ForgetPass.class));
    }


//    // Declare a string variable for the key we’re going to use in our fingerprint authentication
//    private static final String KEY_NAME = "yourKey";
//    private Cipher cipher;
//    private KeyStore keyStore;
//    private KeyGenerator keyGenerator;
//    private TextView textView;
//    private FingerprintManager.CryptoObject cryptoObject;
//    private FingerprintManager fingerprintManager;
//    private KeyguardManager keyguardManager;
//
////Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void generateKey() throws FingerprintException {
//        try {
//            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//
//            //Generate the key//
//            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
//
//            //Initialize an empty KeyStore//
//            keyStore.load(null);
//
//            //Initialize the KeyGenerator//
//            keyGenerator.init(new
//
//                    //Specify the operation(s) this key can be used for//
//                    KeyGenParameterSpec.Builder(KEY_NAME,
//                    KeyProperties.PURPOSE_ENCRYPT |
//                            KeyProperties.PURPOSE_DECRYPT)
//                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//
//                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
//                    .setUserAuthenticationRequired(true)
//                    .setEncryptionPaddings(
//                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                    .build());
//
//            //Generate the key//
//            keyGenerator.generateKey();
//
//        } catch (KeyStoreException
//                | NoSuchAlgorithmException
//                | NoSuchProviderException
//                | InvalidAlgorithmParameterException
//                | CertificateException
//                | IOException exc) {
//            exc.printStackTrace();
//            throw new FingerprintException(exc);
//        }
//    }
//
//    //Create a new method that we’ll use to initialize our cipher//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public boolean initCipher() {
//        try {
//            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
//            cipher = Cipher.getInstance(
//                    KeyProperties.KEY_ALGORITHM_AES + "/"
//                            + KeyProperties.BLOCK_MODE_CBC + "/"
//                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
//        } catch (NoSuchAlgorithmException |
//                NoSuchPaddingException e) {
//            throw new RuntimeException("Failed to get Cipher", e);
//        }
//
//        try {
//            keyStore.load(null);
//            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
//                    null);
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            //Return true if the cipher has been initialized successfully//
//            return true;
//        } catch (KeyPermanentlyInvalidatedException e) {
//
//            //Return false if cipher initialization failed//
//            return false;
//        } catch (KeyStoreException | CertificateException
//                | UnrecoverableKeyException | IOException
//                | NoSuchAlgorithmException | InvalidKeyException e) {
//            throw new RuntimeException("Failed to init Cipher", e);
//        }
//    }
//
//    private class FingerprintException extends Exception {
//        public FingerprintException(Exception e) {
//            super(e);
//        }
//    }
}