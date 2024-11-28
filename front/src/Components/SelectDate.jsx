import { useState } from "react";
import InputField from "./InputField";
import useNotificationStore from "../store/useNotificationStore";
import BookingCalendar from "./BookingCalendar";

const SelectDate = ({ onClose, isOpen }) => {
  const { setNotification } = useNotificationStore();
  const [bookingDates, setBookingDates] = useState([]);
  const calendarStyles = `scale-90 relative rounded-xl max-[500px]:scale-[0.75] md:right-auto transition-all duration-300 ease-in-out`;

  const [feature, setFeature] = useState({
    id: null,
    name: "",
    icon: null,
    hasQuantity: false,
  });
  const [errors, setErrors] = useState({
    name: "",
    icon: null,
    hasQuantity: false,
  });

  const handleFeatureChange = (e) => {
    const { name, value } = e.target;
    setFeature((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    setBookingDates(["", ""]);
    onClose();
  };

  const handleCloseForm = () => {
    setFeature({ name: "", icon: null, hasQuantity: false });
    setBookingDates(["", ""]);
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="animate-fadeIn hs-overlay fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 py-8 z-50 backdrop-blur">
      <div className="sm:max-w-lg sm:w-full m-3 sm:mx-auto">
        <div className="flex flex-col items-center justify-center p-6">
          <div className="w-full max-w-lg bg-white p-6 rounded-lg shadow-md relative z-10">
            <div className="absolute top-0 left-0 right-0 bg-primary-color p-4 rounded-t-lg text-white z-20">
              <div className="flex items-center justify-between">
                <h2 className="text-2xl font-light font-montserrat">
                  Seleccione una fecha
                </h2>
                <button
                  onClick={handleCloseForm}
                  className="w-6 h-6 rounded-full flex items-center justify-center text-primary font-semibold"
                >
                  <img
                    src="/Icons/cerrar.svg"
                    alt="Cerrar"
                    className="w-6 h-6"
                  />
                </button>
              </div>
            </div>

            <div className="pt-16">
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className=" flex flex-col gap-6 justify-center items-center">
                  <BookingCalendar
                    setBookingDates={setBookingDates}
                    visible={true}
                    setVisible={() => {}}
                    calendarStyles={calendarStyles}
                    hasReserves={true}
                  />
                  <span className="flex gap-10">
                    <InputField
                      id="name"
                      name="name"
                      placeholder="Fecha de llegada"
                      value={bookingDates[0]}
                      onChange={handleFeatureChange}
                      className="w-full border border-[#A9AEB9] rounded p-2.5 font-normal text-black font-montserrat"
                      readOnly
                    />
                    <InputField
                      id="name"
                      name="name"
                      placeholder="Fecha de salida"
                      value={bookingDates[1]}
                      onChange={handleFeatureChange}
                      className="w-full border border-[#A9AEB9] rounded p-2.5 font-normal text-black font-montserrat"
                      readOnly
                    />
                  </span>

                  {errors.name && (
                    <p className="text-red-500 text-sm mt-1">{errors.name}</p>
                  )}
                </div>
                <div className="flex justify-center pb-5">
                  <button
                    type="submit"
                    className="w-full py-3 px-6 rounded-lg bg-primary-color text-background-white"
                  >
                    Reservar
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SelectDate;
