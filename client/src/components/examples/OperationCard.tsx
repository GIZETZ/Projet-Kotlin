import OperationCard from '../OperationCard';

export default function OperationCardExample() {
  const mockOperation = {
    id: 1,
    nom: "Cotisation Janvier 2025",
    type: "ADHESION" as const,
    montantCible: 50000,
    montantCollecte: 32500,
    dateDebut: new Date(2025, 0, 1),
    dateFin: new Date(2025, 0, 31),
    etat: "EN_COURS" as const,
    nombrePayeurs: 13,
  };

  return (
    <div className="p-6 max-w-lg">
      <OperationCard
        operation={mockOperation}
        onViewDetails={() => console.log('View details clicked')}
        onExport={() => console.log('Export clicked')}
        onEdit={() => console.log('Edit clicked')}
      />
    </div>
  );
}
