import { Home, Plus, FileText, Settings } from "lucide-react";
import { Button } from "@/components/ui/button";

interface BottomNavProps {
  activeTab: string;
  onTabChange: (tab: string) => void;
}

export default function BottomNav({ activeTab, onTabChange }: BottomNavProps) {
  const navItems = [
    { id: "dashboard", label: "Accueil", icon: Home },
    { id: "add", label: "Paiement", icon: Plus, primary: true },
    { id: "reports", label: "Rapports", icon: FileText },
    { id: "settings", label: "Paramètres", icon: Settings },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-card border-t border-border z-50 md:hidden">
      <div className="flex items-center justify-around p-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;

          if (item.primary) {
            return (
              <Button
                key={item.id}
                onClick={() => onTabChange(item.id)}
                size="icon"
                className="w-14 h-14 rounded-full shadow-lg"
                data-testid={`button-nav-${item.id}`}
              >
                <Icon className="w-6 h-6" />
              </Button>
            );
          }

          return (
            <button
              key={item.id}
              onClick={() => onTabChange(item.id)}
              className={`flex flex-col items-center gap-1 p-2 min-w-[70px] rounded-lg transition-colors ${
                isActive
                  ? "text-primary"
                  : "text-muted-foreground hover-elevate"
              }`}
              data-testid={`button-nav-${item.id}`}
            >
              <Icon className="w-5 h-5" />
              <span className="text-xs font-medium">{item.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
