interface StatCardProps {
  label: string;
  value: number;
  currency?: boolean;
  variant?: "default" | "success" | "warning";
}

export default function StatCard({ label, value, currency = true, variant = "default" }: StatCardProps) {
  const variantClasses = {
    default: "text-foreground",
    success: "text-chart-2",
    warning: "text-chart-3",
  };

  const formattedValue = currency
    ? new Intl.NumberFormat("fr-FR", { style: "currency", currency: "XAF" }).format(value)
    : value.toLocaleString("fr-FR");

  return (
    <div className="space-y-1">
      <p className="text-sm text-muted-foreground">{label}</p>
      <p className={`text-2xl font-bold tabular-nums ${variantClasses[variant]}`} data-testid={`stat-${label.toLowerCase().replace(/\s+/g, '-')}`}>
        {formattedValue}
      </p>
    </div>
  );
}
