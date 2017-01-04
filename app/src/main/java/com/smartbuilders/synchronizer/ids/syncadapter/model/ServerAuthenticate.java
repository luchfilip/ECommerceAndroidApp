package com.smartbuilders.synchronizer.ids.syncadapter.model;

import com.smartbuilders.synchronizer.ids.model.User;

import android.content.Context;

/**
 * before 27.02.2016
 * @author jsarco
 *
 */
public interface ServerAuthenticate {
	
	/**
	 * Se usa para obtener el authToken cuando el usuario esta accediendo por primera vez al sistema
	 * @param user
	 * @param ctx
	 * @throws Exception
	 */
    void userSignUp(final User user, final String authType, Context ctx) throws Exception;

    /**
     * Se usa para verificar el authToken del usuario en el sistema
     * @param user
     * @param authType
     * @param ctx
     * @throws Exception
     */
    void userSignIn(final User user, final String authType, Context ctx) throws Exception;

	/**
	 * Se usa para recuperar el authToken del usuario del servidor
	 * @param user
	 * @param authType
	 * @param ctx
	 * @throws Exception
	 */
	void userGetAuthToken(final User user, final String authType, Context ctx) throws Exception;
    
    /**
     * Se usa para avisarle al servidor la sincronizacion finalizo
     * @param user
     * @param syncState
     * @param ctx
     * @throws Exception
     */
    void userSignOut(final User user, final String syncState, Context ctx) throws Exception;
}
