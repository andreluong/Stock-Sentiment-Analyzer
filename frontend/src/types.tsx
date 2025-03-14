export type Article = {
	id: number;
	headline: string;
	description: string;
	publisher: string;
	timeline: number[];
	url: string;
	sentiment: number;
	symbol: string;
};

export type Stock = {
	id: number;
	symbol: string;
	positives: number;
	neutrals: number;
	negatives: number;
	score: number;
}

export type StockAnalysis = {
	stock: Stock;
	articleList: Article[];
}

export type Scores = {
	positive: string;
	weighted: string;
	decay: string;
};