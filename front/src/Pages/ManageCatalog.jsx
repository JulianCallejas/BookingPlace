import { useEffect, useState } from "react";
import { rustikApi } from "../services/rustikApi";
import { rustikEndpoints } from "../services/rustkEndPoints"
import useNotificationStore from "../store/useNotificationStore";

import AddProductModal from "../Components/AddProductModal";
import useLoaderModalStore from "../store/useLoaderModalStore";
import PageTitleAndBack from "../Components/PageTitleAndBack";


const ManageCatalog = () => {
    const [isModalOpen, setModalOpen] = useState(false);
    const { setNotification } = useNotificationStore();
    const { showLoaderModal, hideLoaderModal } = useLoaderModalStore();
    const [isEditing, setIsEditing] = useState(false);
    const [currentData, setCurrentData] = useState(null); 

    const handleOpenModal = (cabin) => {
        if(cabin && cabin.id){
            setIsEditing(true);
            setCurrentData(cabin);
        } else {
            setIsEditing(false);
            setCurrentData(null);
        }
        setModalOpen(true);
        window.scrollTo(0, 0);
    }
    const handleCloseModal = () => setModalOpen(false);

    const [cabins, setCabins] = useState([]);

    const handleDelete = async (id) => {
        const confirma = confirm("Confirmar eliminar cabaña")
        if (!confirma) return;
        try {
            showLoaderModal();
            await rustikApi.delete(`${rustikEndpoints.cabins}/${id}`);
            const updatedCabins = cabins.filter((cabin) => cabin.id !== id);
            setCabins(updatedCabins);
            setNotification({
                visibility: true,
                type: "success",
                text: "Cabaña eliminada correctamente.",
            });
        } catch (error) {
            console.error("Error al borrar, intente más tarde", error);
        } finally {
            hideLoaderModal();
        }
    };

    useEffect(() => {
        const fetchCabins = async () => {
            try {
                const { data } = await rustikApi.get(rustikEndpoints.cabins);
                setCabins(data);
            } catch (error) {
                console.error("Error al llamar a la api", error);
            }
        };

        !isModalOpen && fetchCabins();

    }, [isModalOpen]);

    return (
        <div className="animate-fadeIn" >
            <div className="container w-screen px-5" >
                <div className="py-8 animate-fadeIn">
                    <div className="flex w-full justify-between items-center">
                        <PageTitleAndBack title={`Mis cabañas`} />
                        <div className="px-5 py-4 flex justify-end">
                            <button
                                className="bg-[#088395] rounded-xl py-2 px-9 max-sm:px-4 text-[#EEEEEEEE]"
                                type="button"
                                onClick={handleOpenModal}
                            >
                                Agregar Cabaña
                            </button>
                        </div>

                    </div>
                    <div className="container mx-auto my-4  bg-light-text rounded-3xl shadow-lg">
                        <table className="w-full rounded-3xl p-2">
                            <thead className=" w-full h-full">
                                <tr>
                                    <th
                                        className="px-5 py-7 text-lg  text-primary-color  tracking-wider text-left font-montserrat">
                                        Cabaña
                                    </th>

                                    <th
                                        className="px-5 py-7 text-lg  text-primary-color  tracking-wider text-center font-montserrat">
                                        Id Cabaña
                                    </th>
                                    <th
                                        className="px-5 py-7 text-lg  text-primary-color  tracking-wider text-center font-montserrat">
                                        Acciones
                                    </th>
                                </tr>
                            </thead> 
                            <tbody className="px-5 bg-white rounded-3xl">
                                {cabins.map((cabin) => (
                                    <tr key={cabin.id} className="border-b border-gray-200 border-[5px]">
                                        <td className="px-5 py-5 flex items-center justify-start gap-7">
                                            <div className="grid grid-cols-[auto_1fr] items-center gap-10" >
                                                <div className="w-40 h-28 relative bg-cover bg-center bg-no-repeat rounded-lg" style={{ backgroundImage: `url(${cabin.images[0].url})` }}>    </div>
                                                <div>
                                                    <p className="text-gray-900 whitespace-no-wrap font-montserrat ">{cabin.name}</p>
                                                    <p className="text-gray-900 whitespace-no-wrap text-xs font-montserrat">{cabin.description}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-5 py-5 min-w-40">
                                            <p className="text-gray-900 whitespace-no-wrap text-center">{cabin.id}</p>
                                        </td>
                                        <td className="px-5 py-5 min-w-40">
                                            <div className="flex justify-center items-center gap-5 my-auto ">
                                                <button
                                                    className="active:scale-90"
                                                    onClick={() => {
                                                        handleOpenModal(cabin);

                                                    }}
                                                >
                                                    <img src="/Icons/Editar.svg" alt="Editar cabaña" />
                                                </button>
                                                <button
                                                    className="active:scale-90"
                                                    onClick={() => handleDelete(cabin.id)}
                                                >
                                                    <img src="/Icons/Eliminar.svg" alt="Eliminar cabaña" />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                 <tr className="h-5 bg-transparent"></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <AddProductModal isOpen={isModalOpen} onClose={handleCloseModal}  currentData={currentData} isEditing={isEditing} />
        </div>
    )
}

export default ManageCatalog;