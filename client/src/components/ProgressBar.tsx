interface ProgressBarProps {
  current: number;
  target: number;
  className?: string;
}

export default function ProgressBar({ current, target, className = "" }: ProgressBarProps) {
  const percentage = Math.min((current / target) * 100, 100);
  const isComplete = current >= target;
  const isNearComplete = percentage >= 80;

  return (
    <div className={`space-y-2 ${className}`}>
      <div className="flex items-center justify-between text-sm">
        <span className="text-muted-foreground">Progression</span>
        <span className="font-semibold tabular-nums">{percentage.toFixed(0)}%</span>
      </div>
      <div className="h-3 bg-muted rounded-full overflow-hidden">
        <div
          className={`h-full transition-all duration-500 ease-out ${
            isComplete
              ? "bg-chart-2"
              : isNearComplete
              ? "bg-chart-3"
              : "bg-primary"
          }`}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
}
