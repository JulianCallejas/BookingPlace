import { Link, Navigate, Outlet, useLocation } from "react-router-dom";
import { useUser } from "../../hooks/useUser";
import { routeList } from "../../helpers/routeList";
import Avatar from "../Avatar";
import AdminMenuOption from "../AdminMenuOption";
import { BookingsIcon } from "../icons/BookingsIcon";
import { CabinsIcon } from "../icons/CabinsIcon";
import { DashboardIcon } from "../icons/DashboardIcon";
import { FeaturesIcon } from "../icons/FeaturesIcon";
import { UsersIcon } from "../icons/UsersIcon";
import { LogoutIcon } from "../icons/LogoutIcon";
import { RkIcon } from "../icons/RkIcon";

const AdminMenuOptions = [
  {
    url: routeList.ADMIN_PANEL,
    icon: <DashboardIcon />,
    text: "Dashboard",
  },
  {
    url: routeList.ADMIN_USERS,
    icon: <UsersIcon />,
    text: "Usuarios",
  },
  {
    url: routeList.ADMIN_FEATURES,
    icon: <FeaturesIcon />,
    text: "Caracteristicas",
  },
  {
    url: routeList.ADMIN_CATALOGS,
    icon: <CabinsIcon />,
    text: "Cabañas",
  },
  {
    url: routeList.ADMIN_BOOKINGS,
    icon: <BookingsIcon />,
    text: "Reservas",
  },
  {
    url: routeList.HOME,
    icon: <RkIcon />,
    text: "Rustik",
  },

];
// className="grid min-h-screen grid-rows-[auto 1fr auto]"
// style={{ display: "grid", gridTemplateRows: "auto 1fr auto", minHeight: "100vh" }}
const AdminLayout = () => {

  const { isAdmin, userLoaded, userName, logout } = useUser();
  const currentLocation = useLocation();

  return (
    <div className="flex w-screen">
      {userLoaded && !isAdmin && <Navigate to={routeList.LOGIN} />}
      <nav className="w-60 h-svh bg-gradient-to-b from-primary-color to-background-dark flex flex-col items-center p-8 gap-12 border-r border-[#2D2F33]" >
        <Link to={routeList.HOME}>
          <img src="/Icons/logoSvg-white.svg" alt="icono" />
        </Link>
        <div className="flex flex-col items-center gap-2">
          <div className="text-3xl" >
          <Avatar size={"medium"} name={userName} />
          </div>
          <p className="capitalize text-xl" >{userName}</p>
          <Link to={routeList.USER_PROFILE} className="px-5 py-2 bg-background-dark rounded-full  text-sm ">
            Editar perfil
          </Link>
        </div>
        <div className="w-full flex flex-col gap-[17px]" >
          {
            AdminMenuOptions.map((option, index) => {
              return (
                <AdminMenuOption key={`adminoption-${index}`} active={currentLocation.pathname === option.url} {...option} />
              )
            })
          }
          </div>
          <br />
         
          <button
            className={`flex w-full items-center gap-3 rounded-lg text-lg px-5 py-3 transition-all hover:bg-secondary-color hover:bg-opacity-15 `}
            onClick={logout}
          >
            <div className="w-5 ">
              <LogoutIcon />
            </div>
            <span className="text-lg overflow-hidden" >
              Salir
            </span>
          </button>
      </nav>
      {
        isAdmin && (
          <div className="w-full ">
            <main className="hidden lg:block w-full max-w-[1360px] mx-auto overflow-x-hidden" >
              <Outlet />
            </main>
            <div className="lg:hidden w-full h-[80vh] flex justify-center items-center flex-col gap-5 p-3" >
              <h1 className="text-3xl text-center font-montserrat " >No puede acceder al panel de administración desde un dispositivo móvil</h1>
              <h2 className="text-2xl text-center text-secondary-color">Ingrese desde una computadora</h2>
            </div>
          </div>
        )}
    </div>

  );
};

export default AdminLayout;
