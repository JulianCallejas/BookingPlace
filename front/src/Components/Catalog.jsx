import { useEffect, useState, } from 'react';
import CarouselModal from './carousel/CarouselModal';
import PageTitleAndBack from './PageTitleAndBack';
import FeatureIcon from './icons/FeatureIcon'
import Policies from './Policies';
import Rating from './Rating';
import Reviews from './Reviews';
import ShareFavButtons from './ShareFavButtons';
import { rustikApi } from '../services/rustikApi';
import { rustikEndpoints } from '../services/rustkEndPoints';
import { getRatingDescription } from '../helpers/getRatingDescription';
import { usePagination } from '../hooks/usePagination';
import SelectDate from './SelectDate';
import { useUser } from '../hooks/useUser';

const Catalog = ({ cabin, getCabin }) => {

    const [showModal, setShowModal] = useState(false);
    const url = (import.meta.env.VITE_RUSTIK_WEB || "") + "/catalogo/" + (cabin.id ?? "");
    const [isFavorite, setIsFavorite] = useState(false);
    const [selectDateIsOpen, setSelectDateIsOpen] = useState(false);

    const { currentData, PaginationControls, setPaginationData  } = usePagination(cabin.ratings);
    const { isLoggedIn } = useUser();
    
    const getFavoritesData = async () => {
        try {
            const { data } = await rustikApi.get(rustikEndpoints.favorites);
            setIsFavorite(data.cabinDTOS.some(favorite => favorite.id === cabin.id));
        } catch (error) {
            console.error("Error al obtener favoritos", error);
        }
    };

    useEffect(() => {
        if(!cabin.ratings) return;
        setPaginationData(cabin.ratings.sort(function(a,b){
            return new Date(b.createdAt) - new Date(a.createdAt)
        }));

    }, [cabin])

    useEffect(() => {
        isLoggedIn && getFavoritesData();

    }, [])

    return (
        <div className="animate-fadeIn w-full py-[26px] px-4 md:px-24 mx-auto">
            <div className='transition-all' >
                <div className='w-full flex flex-col md:flex-row justify-between md:items-center mb-8'>
                    <PageTitleAndBack title={`${cabin.category} / ${cabin.name}`} />
                    <div className='self-end md:self-auto' >
                        <ShareFavButtons cabin={cabin} url={url} isFavorite={isFavorite} refreshFavoritos={getFavoritesData} />
                    </div>
                </div>
                <div className="relative overflow-hidden">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                        {/* Imggrabde */}
                        <div className="h-64 md:h-96 overflow-hidden rounded-[1.2rem]">
                            <img className="object-cover h-full w-full rounded-[1.2rem]" src={cabin.images[0].url} alt={`Foto 1`} />
                        </div>
                        {/* Imgpeque */}
                        <div className="grid grid-rows-2 gap-2">
                            <div className="grid grid-cols-2 gap-2">
                                <div className="h-32 md:h-48 overflow-hidden rounded-[1.2rem] border-4 border-[#FBFFBD]">
                                    <img className="object-cover h-full w-full rounded-[1.2rem]" src={cabin.images[1].url} alt={`Foto 2`} />
                                </div>
                                <div className="h-32 md:h-48 overflow-hidden rounded-[1.2rem] border-4 border-[#FBFFBD]">
                                    <img className="object-cover h-full w-full rounded-[1.2rem]" src={cabin.images[2].url} alt={`Foto 3`} />
                                </div>
                            </div>
                            <div className="grid grid-cols-2 gap-2">
                                <div className="h-32 md:h-48 overflow-hidden rounded-[1.2rem] border-4 border-[#FBFFBD]">
                                    <img className="object-cover h-full w-full rounded-[1.2rem]" src={cabin.images[3].url} alt={`Foto 4`} />
                                </div>
                                <div className="relative h-32 md:h-48 overflow-hidden rounded-[1.2rem] border-4 border-[#FBFFBD]">
                                    <img className="object-cover h-full w-full rounded-[1.2rem]" src={cabin.images[4].url} alt={`Foto 5`} />
                                    <button
                                        className="absolute bottom-2 right-2 bg-[#088395] text-white py-1 px-3 rounded"
                                        onClick={() => setShowModal(true)}
                                    >
                                        Galería
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Detalles */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center my-5 ">
                    <div className="mb-4 md:mb-0">
                        <div className="flex flex-col">
                            <h2 className="montserrat text-2xl md:text-[33px] font-bold text-[#088395] mb-1">
                                {cabin.name} <span className="block md:inline font-roboto text-base text-light-text md:ml-[10px]" >Categoria / <span className="font-roboto text-base text-secondary-color capitalize">{cabin.category.toLowerCase()}</span> </span>
                            </h2>
                            <p className="text-[#EEEEEE] flex items-center my">
                                <img src="/Icons/gps.svg" alt="gps" className="mr-2" />
                                {cabin.location}
                            </p>
                        </div>
                        <div className="flex items-center mt-2">
                            <span className="border border-[#FBFFBD] text-[#088395] font-semibold p-2 rounded mr-2">
                                {cabin.averageScore && cabin.averageScore.toFixed(1)}
                            </span>
                            <span className="text-[#EEEEEE] montserrat text-sm ">{getRatingDescription(cabin.averageScore)} </span>
                            <span className="text-[#088395] ml-2 text-sm">{cabin.totalRatings} reseñas</span>
                        </div>
                    </div>
                    <div className="flex flex-col items-start md:items-end">
                        <h3 className="text-lg md:text-xl font-semibold text-[#088395]">
                            <span className="text-xl md:text-2xl">${cabin.price}</span>
                            <span className="text-sm md:text-lg font-light"> p/noche</span>
                        </h3>
                        <button className="mt-2 bg-[#FBFFBD] text-[#0C1123] h-10 px-4 rounded font-semibold" onClick={setSelectDateIsOpen}>
                            Reservar
                        </button>
                        <SelectDate isOpen={selectDateIsOpen} onClose={setSelectDateIsOpen} />
                    </div>
                </div>
                

                <h2 className="text-xl md:text-2xl font-bold text-[#088395] mb-1">Detalles</h2>
                <p className="py-4 text-[#EEEEEE] text-sm md:text-base">
                    {cabin.description}
                </p>

                <h2 className="text-xl md:text-2xl font-bold text-[#088395] my-4">Características</h2>
                <div className='mb-14' >

                    <ul className='grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-x-4 gap-y-8 '>
                        {cabin.cabinFeatures && cabin.cabinFeatures.sort((a, b) => b.hasQuantity - a.hasQuantity).map((feature) => (
                            <li key={feature.id} className="text-light-text text-lg md:text-xl flex items-center">
                                <span className='text-[2.5rem] text-secondary-color mr-[14px]' ><FeatureIcon id={feature.featureIcon} /></span>
                                <p className='text-wrap' >
                                    {feature.featureName} {feature.quantity ? <span className='border border-secondary-color px-3 py-1 rounded-md max-h-12' > {feature.quantity}</span> : ''}
                                </p>
                            </li>
                        ))}
                    </ul>
                </div>
                <Policies />
                <div className="mt-16">
                    <Rating score={cabin.averageScore} totalRatings={cabin.totalRatings} getCabin={getCabin} />
                </div>
                <div className='transition-all' >
                {
                    currentData.length > 0 && currentData.map(review => {
                        return (
                            <div key={review.id} >
                                <Reviews review={review} /></div>
                        )
                    })
                }
                </div>
                <PaginationControls />
                
            </div>
            
            <CarouselModal cabin={cabin} showCarousel={showModal} onClose={() => setShowModal(false)} />
        </div>
    );
};

export default Catalog;
