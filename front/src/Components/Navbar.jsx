import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useUser } from "../hooks/useUser";
import { routeList } from "../helpers/routeList";
import Avatar from "./Avatar";
import CloseButton from "./CloseButton";

const Navbar = () => {
  const navigate = useNavigate();

  const { isLoggedIn, isAdmin, logout, userName } = useUser();
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const onLogin = () => {
    navigate(routeList.LOGIN);
  };

  const onSignIn = async () => {
    // alert("Aqui va el sign in");
    navigate(routeList.REGISTER);
  };

  const handleMouseEnter = () => {
    setUserMenuOpen(true);
  };

  const handleMouseLeave = () => {
    setUserMenuOpen(false);
  };

  const MenuOption = ({ imgUrl, children, onClick }) => (
    <div
      className="relative flex items-center gap-3 cursor-pointer hover:backdrop-brightness-75  p-2 rounded"
      onClick={onClick}
    >
      <img src={imgUrl} alt="Icon" className="w-6 h-6" />
      <span className="text-lg">{children}</span>
      {onClick == logout || (
        <img
          src="/Icons/arrowright.svg"
          alt="Icon"
          className="w-3 h-3 absolute right-0"
        />
      )}
    </div>
  );

  return (
    <nav className="flex justify-between items-center px-4 md:px-24 py-[26px]">
      <Link to="/">
        <img src="/Icons/logoSvg.svg" alt="logo Rustik" />
      </Link>

      <button className="md:hidden z-10" onClick={handleMouseEnter}>
        <img src="/Icons/hamburger.svg" alt="icono" />
      </button>

      {!isLoggedIn && (
        <div className="gap-5 hidden md:flex">
          <button
            className="bg-[#088395] rounded-xl py-2 px-9 text-[#EEEEEEEE]"
            type="button"
            onClick={onLogin}
          >
            Iniciar Sesión
          </button>
          <button
            className="bg-[#FBFFBD] rounded-xl py-2 px-9 text-[#112211]"
            type="button"
            onClick={onSignIn}
          >
            Registrarse
          </button>
        </div>
      )}

      {isLoggedIn && (
        <div
          className="max-md:hidden gap-2 flex justify-center items-center cursor-pointer"
          onClick={handleMouseEnter}
        >
          <span className="relative">
            <Avatar name={userName} size="small" />
            <img
              className="absolute bottom-0 right-0 w-4 h-4"
              src="/Icons/arrowdown.svg"
              alt="arrow down"
            />
          </span>
          <p className="text-[1.2rem] capitalize">{userName}</p>
        </div>
      )}
      <section
        className={`absolute flex flex-col rounded-[0.75rem] bg-primary-color p-8 w-[23rem]  top-[5.5rem] right-0 md:top-[5rem] md:right-24 transition-all duration-200 ease-in-out ${userMenuOpen
          ? "opacity-100 translate-y-0 visible"
          : "opacity-0 -translate-y-5 invisible"
          }`}
        onMouseLeave={handleMouseLeave} // Ensure menu closes when mouse leaves the section
        onClick={handleMouseLeave}
      >
        <div className="relative" >
          <div className="absolute -top-3 -right-3" >
            <CloseButton callback={() => setUserMenuOpen(false)} />
          </div>
          {isLoggedIn ? (
            <>
              <span className="text-[1.3rem] font-medium flex items-center gap-5 w-full">
                <div className="shadow-md rounded-full shadow-[rgba(0,0,0,0.6)]">
                  <Avatar name={userName} size="medium" />
                </div>

                <p className="capitalize" >{userName}</p>
              </span>
              <hr className="border-background-dark border-dotted opacity-[15%] mt-6 mb-4" />
              <Link to={routeList.USER_PROFILE}>
                <MenuOption imgUrl="/Icons/profile.svg">Mi Cuenta</MenuOption>
              </Link>
              <Link to={routeList.USER_FAVORITES}>
                <MenuOption imgUrl="/Icons/heart.svg">Favoritos</MenuOption>
              </Link>
              {isAdmin && (
                <Link to={routeList.ADMIN_PANEL}>
                  <MenuOption imgUrl="/Icons/config.svg">
                    Panel de Admin
                  </MenuOption>
                </Link>
              )}
              <hr className="border-background-dark border-dotted opacity-[15%] my-4" />
              <MenuOption imgUrl="/Icons/logout.svg" onClick={logout}>
                Cerrar Sesión
              </MenuOption>

            </>
          ) : (
            <>
              <hr className="border-background-dark border-dotted opacity-[15%] mt-6 mb-4" />
              <Link to={routeList.LOGIN}>
                <MenuOption imgUrl="/Icons/login-icon.svg">Iniciar sesión</MenuOption>
              </Link>
              <Link to={routeList.REGISTER}>
                <MenuOption imgUrl="/Icons/register-icon.svg">Registrarse</MenuOption>
              </Link>
            </>
          )}
        </div>
      </section>
    </nav>
  );
};

export default Navbar;
