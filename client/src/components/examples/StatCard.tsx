import StatCard from '../StatCard';

export default function StatCardExample() {
  return (
    <div className="grid grid-cols-3 gap-6 p-6 max-w-2xl">
      <StatCard label="Ciblé" value={50000} />
      <StatCard label="Collecté" value={32500} variant="success" />
      <StatCard label="Restant" value={17500} variant="warning" />
    </div>
  );
}
