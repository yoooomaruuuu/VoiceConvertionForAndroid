#pragma once
#include <memory>
#include "world_harvest_f0_impl.hpp"
#include "define_macro.hpp"

namespace audio_analysis_lib
{
    using namespace my_world_voice_feature;
	class harvest_f0_impl;

	class harvest_f0
	{
	public:
		harvest_f0(int init_fs, int init_frame_period);
		void f0_estimate(const double *x, int x_length, double *temporal_positions, double *f0);
		int get_f0_sample_num(int x_length);
		~harvest_f0();
	private:
		std::unique_ptr<my_world_voice_feature::harvest_f0_impl> pimpl;
	};

	DLLEXPORT void create_harvest_f0(int fs, int frame_period, void** func_object);
	DLLEXPORT void f0_estimate(const double* x, int x_length, double* temporal_positions, double* f0, void* func_object);
	DLLEXPORT void get_f0_sample_num(int x_length, void* func_object);
	DLLEXPORT void delete_harvest_f0(void** func_object);
}
