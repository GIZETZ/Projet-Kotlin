import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import OperationBadge from "./OperationBadge";
import ProgressBar from "./ProgressBar";
import StatCard from "./StatCard";
import { FileText, Share2, Edit, Clock } from "lucide-react";
import { format } from "date-fns";
import { fr } from "date-fns/locale";

export interface Operation {
  id: number;
  nom: string;
  type: "ADHESION" | "FONDS_CAISSE" | "COTISATION_EXCEPTIONNELLE";
  montantCible: number;
  montantCollecte: number;
  dateDebut: Date;
  dateFin: Date;
  etat: "EN_COURS" | "CLOTURE";
  nombrePayeurs: number;
}

interface OperationCardProps {
  operation: Operation;
  onViewDetails?: () => void;
  onExport?: () => void;
  onEdit?: () => void;
}

export default function OperationCard({ operation, onViewDetails, onExport, onEdit }: OperationCardProps) {
  const montantRestant = operation.montantCible - operation.montantCollecte;
  const isActive = operation.etat === "EN_COURS";
  const percentage = (operation.montantCollecte / operation.montantCible) * 100;
  
  // Déterminer la variante de la barre de progression
  let progressVariant: "primary" | "success" | "warning" = "primary";
  if (percentage >= 100) {
    progressVariant = "success";
  } else if (percentage >= 80) {
    progressVariant = "warning";
  }

  return (
    <Card className="hover-elevate active-elevate-2" data-testid={`card-operation-${operation.id}`}>
      <CardHeader className="space-y-4">
        <div className="flex items-start justify-between gap-3">
          <div className="space-y-2 flex-1 min-w-0">
            <h3 className="text-xl font-semibold leading-tight" data-testid="text-operation-name">{operation.nom}</h3>
            <div className="flex items-center gap-2 flex-wrap">
              <OperationBadge type={operation.type} />
              {!isActive && (
                <span className="inline-flex items-center gap-1 text-xs text-muted-foreground">
                  <Clock className="w-3 h-3" />
                  Clôturée
                </span>
              )}
            </div>
          </div>
          {onEdit && isActive && (
            <Button size="icon" variant="ghost" onClick={onEdit} className="shrink-0" data-testid="button-edit-operation">
              <Edit className="w-4 h-4" />
            </Button>
          )}
        </div>
        <div className="text-sm text-muted-foreground">
          {format(operation.dateDebut, "d MMM", { locale: fr })} - {format(operation.dateFin, "d MMM yyyy", { locale: fr })}
        </div>
      </CardHeader>

      <CardContent className="space-y-5">
        <div className="grid grid-cols-3 gap-3 sm:gap-4">
          <StatCard label="Ciblé" value={operation.montantCible} />
          <StatCard label="Collecté" value={operation.montantCollecte} variant="success" />
          <StatCard label="Restant" value={montantRestant} variant={montantRestant > 0 ? "warning" : "success"} />
        </div>

        <ProgressBar 
          current={operation.montantCollecte} 
          target={operation.montantCible}
          variant={progressVariant}
        />

        <div className="flex items-center gap-2 text-sm sm:text-base">
          <span className="font-semibold text-foreground">{operation.nombrePayeurs}</span>
          <span className="text-muted-foreground">payeur{operation.nombrePayeurs > 1 ? "s" : ""}</span>
        </div>
      </CardContent>

      <CardFooter className="flex gap-3 flex-wrap">
        <Button onClick={onViewDetails} className="flex-1" data-testid="button-view-details">
          <FileText className="w-4 h-4 mr-2" />
          Détails
        </Button>
        {onExport && (
          <Button onClick={onExport} variant="outline" data-testid="button-export">
            <Share2 className="w-4 h-4 mr-2" />
            Partager
          </Button>
        )}
      </CardFooter>
    </Card>
  );
}
