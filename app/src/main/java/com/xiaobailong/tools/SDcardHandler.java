package com.xiaobailong.tools;

/**
 * Created by dongyuangui on 15-4-16.
 */
public class SDcardHandler {
//    private static SDcardHandler instance;
//    private Context mContext;
//
//    private String getAdhocFileDir(){
////        File fileadhoc = new File(getSDPath()+"/Adhoc");
//        String fileDir = null;
//        if(hasSdcard()){
//            File fileadhoc = Environment.getExternalStoragePublicDirectory("Adhoc");
//            if(!fileadhoc.exists()){
//                fileadhoc.mkdirs();
//            }
//            fileDir = fileadhoc.getAbsolutePath();
//        }
//
//        return fileDir;
//    }
//
//    private SDcardHandler(Context context){
//
//        mContext = context;
//    }
//
//    public static SDcardHandler getInstance(Context context){
//
//        if(instance == null){
//            instance = new SDcardHandler(context);
//        }
//        return instance;
//    }
//    // 从ADHOC_CLIENT_ID文件读取ClientID
//    public String readFile(){
//
//        String adhocdir = getAdhocFileDir();
//        return readFile(adhocdir+"/"+AdhocConstants.FILE_CLIENT_ID);
//    }
//
//    /*
//     * Read content from file. Returns null if it fails to load.
//     */
//    private String readFile(String dir) {
//        String content = null;
//
//        // 没有挂载可读写sdcard 返回空
//        if(!hasSdcard()){
//            T.e(new Exception("读存储卡失败 存储卡挂载状态 " +SDcardHandler.getInstance(mContext).hasSdcard()));
//            return null;
//        }
//
//        try {
//            File file = new File(dir);
//            if(!file.exists()){
//                return null;
//            }
//
//            FileInputStream inputStream = new FileInputStream(file);
//            byte[] buffer = new byte[1024];
//            int readLen = inputStream.read(buffer, 0, 1024);
//            if (readLen <= 0 || inputStream.available() > 0) {
//                T.i("Either file (" + dir + ") is too long or it is corrupted.");
//                inputStream.close();
//            } else {
//                content = new String(buffer, 0, readLen);
//                inputStream.close();
//            }
//        } catch (IOException e) {
//           T.e(new Exception("Fails to read file: " + dir));
//        }
//        return content;
//    }
//
//
//    // 从AdhocConstants.FILE_CLIENT_ID 中读取字符串
//    public boolean writeFile(String data){
//        String adhocdir = getAdhocFileDir();
//        return writeFile(adhocdir+"/"+AdhocConstants.FILE_CLIENT_ID,data);
//    }
//
//    /*
//     * Writes a string into a file. If failed, return false.
//     */
//    private boolean writeFile(String dir, String data) {
//        // 没有挂载sdcard 返回false
//        if(!hasSdcard()){
//            T.e(new Exception("写SDCARD文件失败！SDCARD 挂载状态 " +SDcardHandler.getInstance(mContext).hasSdcard()));
//            return false;
//        }
//        File newFile = new File(dir);
//        if(newFile.exists()){
//            newFile.delete();
//            T.i("删除文件" + dir);
//        }
//
//        try {
//            newFile.createNewFile();
//            FileOutputStream outStream =new FileOutputStream(newFile);
//            outStream.write(data.getBytes());
//            outStream.close();
//            return true;
//        } catch (IOException e) {
//            T.e(e);
//            newFile.delete();
//            return false;
//        }
//    }
//
//    // 返回是否安装sdcard并且可以读写
//    public boolean hasSdcard() {
//        String status = Environment.getExternalStorageState();
//        T.i("SDCARD 挂载状态 " +status);
//        if (status.equals(Environment.MEDIA_MOUNTED)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    // 返回sdcard 路径 mnt/sdcard
//    public String getSDPath(){
//        String sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState()
//                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
//        if (sdCardExist) {
//            File sdDirfile = Environment.getExternalStorageDirectory();//获取跟目录
//            sdDir = sdDirfile.getAbsolutePath();
//        }
//        return sdDir;
//
//    }
}
