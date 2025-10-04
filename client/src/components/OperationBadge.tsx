import { Badge } from "@/components/ui/badge";

type OperationType = "ADHESION" | "FONDS_CAISSE" | "COTISATION_EXCEPTIONNELLE";

interface OperationBadgeProps {
  type: OperationType;
}

const badgeConfig: Record<OperationType, { label: string; className: string }> = {
  ADHESION: {
    label: "Adhésion",
    className: "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300",
  },
  FONDS_CAISSE: {
    label: "Fonds de caisse",
    className: "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300",
  },
  COTISATION_EXCEPTIONNELLE: {
    label: "Cotisation",
    className: "bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-300",
  },
};

export default function OperationBadge({ type }: OperationBadgeProps) {
  const config = badgeConfig[type];
  
  return (
    <Badge className={`${config.className} no-default-hover-elevate no-default-active-elevate`} data-testid={`badge-type-${type.toLowerCase()}`}>
      {config.label}
    </Badge>
  );
}
