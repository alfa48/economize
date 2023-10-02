package ao.znt.econ.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.format.DateUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ao.znt.econ.R;

public class Util {
    public static final String CHAVE_TELA = "telaPrev";
    public static final String VALOR_TELA_MAIN = "Main";
    public static final String CHAVE_TELA_UI_COMPRA = "UI_COMPRA";
    public static final String CHAVE_CATEGORIA = "CATEGORIA";
    public static final String VALOR_TELA_UI_COMPRA_ACAO_SETCATEGORIA = "ACAO_SETCATEGORIA";
    public static final String CHAVE_ORCAMENTO_ID = "_id_orcamento";
    public static final String VALOR_ORCAMENTO_LISTAR_COMPRAS = "VALOR_ORCAMENTO_LISTAR_COMPRAS";
    public static final String CHAVE_ORCAMENTO_LISTAR_COMPRAS = "VALOR_ORCAMENTO_LISTAR_COMPRAS";
    public static final String CHAVE_COMPRA_ID = "_id_compra";
    public static Resources rs;

    public Util(Context context){
      this.rs = context.getResources();
    }
    public static String cripto(String senha) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] arrayDeByte = messageDigest.digest(senha.getBytes("UTF-8"));

        StringBuilder stringBuilder = new StringBuilder();
        for (byte by: arrayDeByte){
            stringBuilder.append(String.format("%02X",0xFF & by));
        }
        return stringBuilder.toString();
    }
    public static String getMesString(int mes){
        String[] array = rs.getStringArray(R.array.meses);

        switch(mes){
            case 1: return array[0];
            case 2: return array[1];
            case 3: return array[2];
            case 4: return array[3];
            case 5: return array[4];
            case 6: return array[5];
            case 7: return array[6];
            case 8: return array[7];
            case 9: return array[8];
            case 10: return array[9];
            case 11: return array[10];
            case 12: return array[11];
        }
        return "Mês desconhecido";
    }
    public static int getMes(Date data){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = sdf.format(data);
        int mes = Integer.parseInt(dataString.split("/")[1]);
        return mes;
    }
    public static String getDataFormatado(Date data){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }
    public static int getAno(Date data){
        return (Integer.parseInt(Util.getDataFormatado(data).split("/")[2]));
    }

    public static String MOEDA(int moeda) {
        String[] array = rs.getStringArray(R.array.moedas);

        switch(moeda){
            case 1:return array[0];
            case 2:return array[1];
            case 3:return array[2];
            case 4:return array[3];
            case 5:return array[4];
            case 6:return array[5];
            case 7:return array[6];
            case 8:return array[7];
            default:return "Moeda_desconhecido";
        }
    }

    public static int getMoeda(String toString) {
            String[] array = rs.getStringArray(R.array.moedas);

            if(toString.equals(array[0])) return 1;
            if(toString.equals(array[1])) return 2;
            if(toString.equals(array[2])) return 3;
            if(toString.equals(array[3])) return 4;
            if(toString.equals(array[4])) return 5;
            if(toString.equals(array[5])) return 6;
            if(toString.equals(array[6])) return 7;
            if(toString.equals(array[7])) return 8;

        return 0;
    }
    public static int getPositionCategoria(String toString) {
        String[] array = rs.getStringArray(R.array.categorias);

        if(toString.equals(array[0])) return 0;
        if(toString.equals(array[1])) return 1;
        if(toString.equals(array[2])) return 2;
        if(toString.equals(array[3])) return 3;
        if(toString.equals(array[4])) return 4;
        if(toString.equals(array[5])) return 5;
        if(toString.equals(array[6])) return 6;
        return -1;
    }
    public static String getDataHumanizada(Date dataEspecifica){
        return DateUtils.getRelativeTimeSpanString(dataEspecifica.getTime(), new Date().getTime(), DateUtils.DAY_IN_MILLIS).toString();
    }

    public static String getCategoria(int categoria) {
        String[] array = rs.getStringArray(R.array.categorias);

        switch(categoria){
            case 0:return array[0];
            case 1:return array[1];
            case 2:return array[2];
            case 3:return array[3];
            case 4:return array[4];
            case 5:return array[5];
            case 6:return array[6];
            default:return "null";
        }
    }
    public static boolean validaSenha(String senha){
        return senha.matches("\\d\\d\\d\\d");
    }
}