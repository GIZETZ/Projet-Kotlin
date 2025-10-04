import { useState } from 'react';
import BottomNav from '../BottomNav';

export default function BottomNavExample() {
  const [activeTab, setActiveTab] = useState('dashboard');

  return (
    <div className="h-screen relative">
      <div className="p-6">
        <p className="text-center text-muted-foreground">
          Onglet actif: <strong>{activeTab}</strong>
        </p>
      </div>
      <BottomNav activeTab={activeTab} onTabChange={setActiveTab} />
    </div>
  );
}
