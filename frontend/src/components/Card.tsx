import { Chip } from '@heroui/chip';
import { useState } from 'react';
import StockModal from './StockModal';
import { Scores, StockAnalysis } from '../types';

export default function Card({
  stockAnalysis,
  imageUrl
}: {
  stockAnalysis: StockAnalysis;
  imageUrl: string;
}) {
  const [isOpen, setIsOpen] = useState(false);

  const stock = stockAnalysis.stock;
  const total = stock.positives + stock.negatives + stock.neutrals;
  const positiveScore = (((stock.positives / total) * 100) || 0).toFixed(1);
  const weightedScore = ((((stock.positives - stock.negatives) / total) * 100) || 0).toFixed(1);
  const decayScore = (stock.score || 0).toFixed(2)
  const scores: Scores = {positive: positiveScore, weighted: weightedScore, decay: decayScore}

  return (
    <>
      <div 
        className="border-2 bg-white p-4 text-start rounded-md space-y-4 hover-shadow-lg hover:cursor-pointer" 
        onClick={() => setIsOpen(true)}
      >
        <div className='border-b-2 pb-4 mb-4'>
          <img
            src={imageUrl}
            alt="Company logo"
            className='w-25 h-25 mx-auto'
          />
        </div>

        {/* Day Change */}
        <div className="justify-between flex flex-row">
            <h1 className="text-2xl font-semibold gradient-text">{stock.symbol.toUpperCase()}</h1>
            <p className="">+2.19%</p>
        </div>
        <div className='flex flex-col font-semibold gap-4'>
          <Chip className="bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow">
            Positive: {positiveScore}%
          </Chip>

          <Chip className="bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow">
            Weighted: {weightedScore}%
          </Chip>

          <Chip className="bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow">
            Score: {decayScore}
          </Chip>
        </div>
        
        {/* Stats */}
        <div>
          <p className='font-semibold text-sm mb-1'>Stats</p>
          <div className="flex flex-row justify-between rounded-md bg-slate-100 p-2 text-sm space-x-3 hover-shadow">
            <div>
              <h1>Positive</h1>
              <p className='text-center text-green-600 font-semibold'>{stock.positives}</p>
            </div>
            <div>
              <h1>Neutral</h1>
              <p className='text-center text-black font-semibold'>{stock.neutrals}</p>
            </div>
            <div>
              <h1>Negative</h1>
              <p className='text-center text-red-600 font-semibold'>{stock.negatives}</p>
            </div>
          </div>
        </div>
      </div>
      {isOpen && 
        <StockModal 
          isOpen={isOpen} 
          setIsOpen={setIsOpen} 
          stockAnalysis={stockAnalysis} 
          scores={scores}
      />}
    </>
  )
}
