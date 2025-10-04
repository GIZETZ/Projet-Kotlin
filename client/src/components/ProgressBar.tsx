interface ProgressBarProps {
  current: number;
  target: number;
  className?: string;
  variant?: "primary" | "success" | "warning";
}

export default function ProgressBar({ current, target, className = "", variant }: ProgressBarProps) {
  const percentage = Math.min((current / target) * 100, 100);
  const isComplete = current >= target;
  const isNearComplete = percentage >= 80;

  // Déterminer la couleur basée sur variant ou statut
  let barColor = "bg-primary";
  if (variant === "success" || isComplete) {
    barColor = "bg-chart-2";
  } else if (variant === "warning" || isNearComplete) {
    barColor = "bg-chart-3";
  } else if (variant === "primary") {
    barColor = "bg-primary";
  }

  return (
    <div className={`space-y-3 ${className}`}>
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium text-muted-foreground">Progression</span>
        <span className="text-base font-bold tabular-nums">{percentage.toFixed(0)}%</span>
      </div>
      <div className="h-4 bg-muted rounded-full overflow-hidden shadow-inner">
        <div
          className={`h-full transition-all duration-500 ease-out ${barColor}`}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
}
