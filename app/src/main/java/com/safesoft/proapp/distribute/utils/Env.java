package com.safesoft.proapp.distribute.utils;

public class Env {
    private static final String URL_BASE = "http://144.91.122.24/";
    public static final String URL_CHECK_VERSION = URL_BASE + "apk/check_version.php";
    public static final String URL_GET_APK_HASH = URL_BASE +"apk/get_apk_hash.php";
    public static final String URL_CHECK_EMAIL = URL_BASE +"backup_cloud/check_email_and_send_code.php";
    public static final String URL_CONNECT_EMAIL = URL_BASE +"backup_cloud/connect_email.php";
    public static final String URL_ADD_EMAIL = URL_BASE +"backup_cloud/add_email.php";
    public static final String URL_UPLOAD_BDD = URL_BASE +"backup_cloud/upload_bdd.php";
    public static final String URL_GET_FILES = URL_BASE +"backup_cloud/get_bdd_list.php";
    public static final String URL_DOWNLOAD_FILES = URL_BASE +"backup_cloud/download_backup.php";
    public static final String hashAlgorithm = "SHA-256";  // Hash algorithm to use
    public static final String APP_VERION = "071125";
    public static final String APP_VERION_LABEL = "V : 07.11.25";

    public static final String MESSAGE_DEMANDE_ACTIVITATION = "Vous êtes en mode évaluation, veuillez activer l'application";
}
