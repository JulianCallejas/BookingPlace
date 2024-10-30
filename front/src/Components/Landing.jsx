import React from "react";

function Landing() {
  return (
    <section className="landing bg-background-dark h-[100vh] max-h-[60rem] flex flex-col justify-center">
      <h1 className="mx-[8rem] text-primary-color font-bold text-[3.3rem] w-fit leading-[4rem]">
        Escapa a la Naturaleza y <br />
        Reserva tu Cabaña ideal
      </h1>
      <p className="mx-[8rem] mt-4 text-light-text text-[1.5rem] w-fit">
        Tu refugio natural, a un clic de distancia.
      </p>
    </section>
  );
}

export default Landing;
