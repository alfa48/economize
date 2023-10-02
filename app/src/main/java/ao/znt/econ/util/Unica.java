package ao.znt.econ.util;

import ao.znt.econ.modelos.Usuario;

public class Unica {
    private static Usuario usuario;

    private Unica(){
    }
    public static synchronized Usuario getInstance(){
        if(usuario==null)
            usuario = new Usuario();
        return usuario;
    }
}
