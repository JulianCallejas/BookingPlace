import { useCallback, useEffect, useState } from "react";
import { useUserStore } from "../store/useUserStore";
import { rustikApi } from "../services/rustikApi";
import { rustikEndpoints } from "../services/rustkEndPoints";
import useNotificationStore from "../store/useNotificationStore";
import useLoaderModalStore from "../store/useLoaderModalStore";

// import { routeList } from "../helpers/routeList";


export const useUser = () => {

    const userLoaded = useUserStore((state) => state.userLoaded);
    const isLoggedIn = useUserStore((state) => state.isLoggedIn);
    const isAdmin = useUserStore((state) => state.isAdmin);
    const userName = useUserStore((state) => state.userName);
    const userEmail = useUserStore((state) => state.userEmail);
    const { setNotification } = useNotificationStore();
    const { showLoaderModal, hideLoaderModal } = useLoaderModalStore();
    const [refreshLoggedUser, setRefreshLoggedUser] = useState(0);

    useEffect(() => {
        const validateToken = async () => {
            try {
                const { data } = await rustikApi.get(rustikEndpoints.validateToken);
                useUserStore.setState((state) => ({ ...state, userLoaded: true, isLoggedIn: true, isAdmin: data.isAdmin, userName: data.name.toLowerCase(), userEmail: data.email }));
            } catch (error) {
                if (error.status >= 500) {
                    console.error(error.message);
                } else if (error.status >= 400) {
                    logout();
                    useUserStore.setState((state) => ({ ...state, userLoaded: true }));
                }
            }
        };
        const token = localStorage.getItem("token");
        !userLoaded && token && validateToken();
    }, [refreshLoggedUser]);

    const setToken = useCallback((token) => {
        return localStorage.setItem("token", token);
    }, []);

    const login = useCallback(async (email, password) => {
        showLoaderModal();
        try {
            const user = { email, password };
            const { data } = await rustikApi.post(rustikEndpoints.login, user);
            setToken(data.token);
            useUserStore.setState({ isLoggedIn: true, isAdmin: data.isAdmin, userName: data.name.toLowerCase(), userEmail: email });
            setNotification({
                visibility: true,
                type: "success",
                text: `¡Bienvenid@, ${data.name}!`,
              });

        } catch (error) {
            if (error.status === 400 || error.status === 404){
                setNotification({
                    visibility: true,
                    type: "error",
                    text: `Credenciales Incorrectas, intente denuevo.`,
                  });
            }
            console.error(error.message);
        }finally{
            hideLoaderModal();
        }

    }, []);

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        useUserStore.setState((state) => ({ ...state, isLoggedIn: false, isAdmin: false, userName: '', userEmail: '' }));
    }, []);

    
    const register = useCallback(async (name, surname, email, phone, password, repeatPassword, country) => {
        showLoaderModal();
        try {
        const user = { name, surname, email, phone, password, repeatPassword, country, isAdmin: false };
            const { data } = await rustikApi.post(rustikEndpoints.register, user);
            setToken(data.token);
            useUserStore.setState((state) => ({ ...state, isLoggedIn: true, isAdmin: data.isAdmin, userName: data.name.toLowerCase(), userEmail: email }));
            setNotification({
                visibility: true,
                type: "success",
                text: `¡Bienvenid@, ${data.name}!`,
              });
        } catch (error) {
            if (error.status >= 400) {
                setNotification({
                    visibility: true,
                    type: "error",
                    text: `${error.response.data} Por favor verifique los datos e intente nuevamente...`,
                  });
            }
            console.error({error});
        }finally{
            hideLoaderModal();
        }

    }, []);


    // const refreshToken = useCallback(async () => {
    //     const token = getToken();
    //     const { data } = await rustikApi.post("/auth/refresh", token);
    //     setToken(data.token);
    // }, []);

    const onRefreshLoggedUser = useCallback( () => {
        useUserStore.setState((state) => ({ ...state, userLoaded: false }));
        setRefreshLoggedUser(refreshLoggedUser + 1);
    }, [refreshLoggedUser]);


    return {
        isLoggedIn,
        isAdmin,
        userName,
        userEmail,
        userLoaded,
        refreshLoggedUser,

        login,
        logout,
        register,
        onRefreshLoggedUser,
        

    }
}
