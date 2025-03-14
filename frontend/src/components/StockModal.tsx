import { Modal, ModalContent, ModalHeader, ModalBody, ModalFooter} from "@heroui/modal";
import { Chip } from '@heroui/chip';
import { Scores, StockAnalysis } from "../types";

export default function StockModal({
  isOpen,
  setIsOpen,
  stockAnalysis,
  scores
} : {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  stockAnalysis: StockAnalysis;
  scores: Scores;
}) {
  const LimitedText = ({ text, limit }: { text: string; limit: number }) => {
    return (
      <p>{text.length > limit ? `${text.slice(0, limit)}...` : text}</p>
    )
  }

  function convertTimelineToDate(timeline: number[]) : string {
    if (timeline.length == 0) return "";

    const date = new Date(Date.UTC(timeline[0], timeline[1]-1, timeline[2], timeline[3], timeline[4], timeline[5]));
    return date.toLocaleString();
  }

  return (
    <Modal 
      isOpen={isOpen} 
      onClose={() => setIsOpen(false)} 
      className="h-3/4 bg-white border-2 rounded-md hover-shadow" 
      classNames={{ closeButton: "hover:bg-neutral-200", backdrop: "backdrop-blur-xs" }}
      size="3xl"
    >
      <ModalContent>
        <ModalHeader className="flex flex-col text-left">
          <h1 className="text-2xl font-bold gradient-text">{stockAnalysis.stock.symbol}</h1>
          <p>Latest News</p>
        </ModalHeader>
        <ModalBody>
          <div className="flex flex-row w-full gap-8 justify-between">
            <Chip className="w-1/5 font-semibold bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow my-auto">
              Positive: {scores.positive}%
            </Chip>
            <Chip className="w-1/5 font-semibold bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow my-auto">
              Weighted: {scores.weighted}%
            </Chip>
            <Chip className="w-1/5 font-semibold bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow my-auto">
              Score: {scores.decay}
            </Chip>

            <div className="w-2/5">
              <div className="flex flex-row justify-between rounded-md bg-slate-100 p-2 text-sm space-x-3 hover-shadow">
                <div>
                  <h1>Positive</h1>
                  <p className='text-center text-green-600 font-semibold'>9</p>
                </div>
                <div>
                  <h1>Neutral</h1>
                  <p className='text-center text-black font-semibold'>4</p>
                </div>
                <div>
                  <h1>Negative</h1>
                  <p className='text-center text-red-600 font-semibold'>6</p>
                </div>
              </div>
            </div>
          </div>
          
          <div className="space-y-3 overflow-y-auto h-[56vh]">
            {stockAnalysis.articleList.map((article, index) => (
              <div 
                className={`border-2 rounded-md hover-shadow hover:cursor-pointer p-2 text-start ${
                  article.sentiment == 1 ? 'border-emerald-500 bg-emerald-50' 
                  : article.sentiment == -1 ? 'border-rose-500 bg-rose-50' 
                  : 'border-slate-500 bg-slate-50'
                }`}
                onClick={() => window.open(article.url, '_blank', 'noopener,noreferrer')}
                key={index}
              >
                <div className="flex flex-row justify-between">
                  <h2 className="text-lg font-semibold">{article.headline}</h2>
                  <Chip className="font-semibold bg-gradient-to-br from-emerald-200 to-blue-200 hover-shadow">
                    {article.sentiment}
                  </Chip>
                </div>
                <LimitedText text={article.description} limit={190} />
                <div className="flex flex-row text-xs mt-1">
                  <p>{article.publisher} • {convertTimelineToDate(article.timeline)}</p>
                  <p></p>
                </div>
              </div>
            ))}
          </div>
          
        </ModalBody>
        <ModalFooter>
        </ModalFooter>
      </ModalContent>
    </Modal>
  )
}
