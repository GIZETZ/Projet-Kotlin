import OperationBadge from '../OperationBadge';

export default function OperationBadgeExample() {
  return (
    <div className="flex flex-wrap gap-3 p-6">
      <OperationBadge type="ADHESION" />
      <OperationBadge type="FONDS_CAISSE" />
      <OperationBadge type="COTISATION_EXCEPTIONNELLE" />
    </div>
  );
}
