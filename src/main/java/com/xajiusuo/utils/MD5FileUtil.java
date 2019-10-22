package com.xajiusuo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MD5FileUtil {

	protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e) {
			log.error(MD5FileUtil.class.getName() + "初始化失败，MessageDigest不支持MD5Util。", e);
		}
	}

	private static void unMap(MappedByteBuffer buffer){
		Cleaner c = ((DirectBuffer)buffer).cleaner();
		if(c != null){
			c.clean();
		}
	}

	public static String getFileMD5String(File file) {
		FileInputStream in = null;
		FileChannel ch = null;
		try {
			in = new FileInputStream(file);
			ch = in.getChannel();
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			messagedigest.update(byteBuffer);
			unMap(byteBuffer);
			return bufferToHex(messagedigest.digest());
		}catch(Exception e) {
			return null;
		}finally{
			close(ch,in);
		}
	}

	public static String getFileMD5String(FileInputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bufferOut = new byte[8192];
		int bytes = 0;
		while((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		String md5 = getMD5String(out.toByteArray());
		close(out);
		return md5;
	}
	
	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	public static String getMD5BufferedImage(BufferedImage image) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", bos);
		}catch(IOException e) {
			return null;
		}
		return MD5FileUtil.getMD5String(bos.toByteArray());
	}
	
	public static String getMD5String(byte[] bytes) {
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for(int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static File newFile(File file){
		if(file != null){
			if(file.exists()){
				return file;
			}
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		return file;
	}

	public static File newFile(String path,String fileName){
		return newFile(new File(path,fileName));
	}


	public static File newFile(String fileName){
		return newFile(new File(fileName));
	}

	public static File newPath(File file){
		if(file.isFile()){
			file.getParentFile().mkdirs();
		}else if(file.isDirectory()){
			file.mkdirs();
		}else if(!file.isFile() && !file.isDirectory()){
			file.getParentFile().mkdirs();
		}
		return file;
	}

	public static File newPath(String fileName){
		return newPath(new File(fileName));
	}

	public static void close(AutoCloseable...cs){
		for(AutoCloseable c:cs){
			try{
				c.close();
			}catch (Exception e){}
		}
	}

	private static ExecutorService pool = Executors.newFixedThreadPool(3);




	public static void delete(String fileName){
		delete(new File(fileName));
	}

	public static void delete(File file){
		if(file.isFile()){
			file.delete();
		}else{
			for(File f:file.listFiles()){
				delete(f);
			}
			file.delete();
		}
	}


	public static void copys(File from,File to){
		if(from.isFile()){
			if(!to.exists()){
				copyf(from,to);
			}
		}else if(from.isDirectory()){
			to.mkdirs();
			to = new File(to,from.getName());
			for(File f:from.listFiles()){
				copys(f,new File(to.getParent(),f.getName()));
			}
		}
	}

	/***
	 * 文件复制
	 * @param from 源文件
	 * @param to 目标文件
	 */
	public static void copyf(File from, File to){
		OutputStream out = null;
		try{
			copyf(new FileInputStream(from),out = new FileOutputStream(newFile(to)));
		}catch (Exception e){
		}finally {
			MD5FileUtil.close(out);
		}
	}

	/***
	 * 通过流进行复制
	 * @param in 输入流 用完后会自动关闭
	 * @param out 输出流,用完不进行关闭
	 */
	public static void copyf(InputStream in, OutputStream out){
		try{
			byte[] buff = new byte[8192];
			int len = 0;
			while((len = in.read(buff)) != -1){
				out.write(buff,0,len);
			}
		}catch (Exception e){
		}finally {
			close(in);
		}
	}


	/***
	 * 文件大小
	 * @param file
	 * @return
     */
	public static long fileSize(File file) {
		long size = 0;
		if(file.isFile()){
			return file.length();
		}else if(file.isDirectory()){
			for(File f:file.listFiles()){
				size += fileSize(f);
			}
		}
		return size;
	}



	private static char[] sizeUnit = new char[]{'B','K','M','G','T'};
	private static NumberFormat numFormat = new DecimalFormat("#,###.##");
	private static Map<String, Long> map = new HashMap<String, Long>();
	static{
		map.put("B", 1l);
		map.put("K", 1024l);
		map.put("M", 1024 * 1024l);
		map.put("G", 1024 * 1024 * 1024l);
		map.put("T", 1024 * 1024 * 1024 * 1024l);
	}

	/***
	 * 对文件大小进行格式化显示
	 * @return
	 */
	public static String l2s(long fileSize){
		double len = fileSize;
		int r = 0;
		while(len > 1024 && r < 4){
			r ++;
			len /= 1024;
		}
		return numFormat.format(len) + sizeUnit[r];
	}

	public static String getContentType(File file) {
		try {
			return java.nio.file.Files.probeContentType(Paths.get(file.getAbsolutePath()));
		} catch (IOException e) {
			return null;
		}
	}

	/***
	 * 文件拿取后要自行删除,否则会常驻硬盘,开发过程请严格检查是否文件能正常删除
	 * @param file
	 * @return
     */
	public static File tempSave(MultipartFile file){
		String name = file.getOriginalFilename();

		String ext = ".tmp";
		if(name.contains(".")){
			ext = name.substring(name.lastIndexOf("."));
		}

		//清理3天未处理的文件
		File dir = path();
		if(dir.isDirectory() && dir.exists()){
			for(File f:dir.listFiles()){
				if(System.currentTimeMillis() - f.lastModified() > 3 * 24 * 60 * 60 * 1000){
					f.delete();
				}
			}
		}

		File temp = new File(path(),System.currentTimeMillis()  + ext);

		try {
			file.transferTo(temp);
		}catch (Exception e){
		}

		temp.deleteOnExit();

		return temp;
	}

	public static InputStream getIn(File file){
		try {
			return new FileInputStream(file);
		}catch (Exception e){
			return null;
		}
	}

	private static File sysTempPath = null;

	/**
	 * 获取临时文件路径,单词上传之后删除
	 * @return
     */
	public static File path(){

		if(sysTempPath != null){
			return sysTempPath;
		}

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = null;
		try {
			Enumeration<URL> urls = loader.getResources("");

			url = urls.nextElement();

			sysTempPath = new File(url.getFile()).getParentFile().getParentFile();
		}catch (Exception e){
			sysTempPath = new File(url.getFile());
		}

		sysTempPath = new File(sysTempPath,"tempUpload");

		if(!sysTempPath.exists()){
			sysTempPath.mkdirs();
		}

		return sysTempPath;
	}

}
